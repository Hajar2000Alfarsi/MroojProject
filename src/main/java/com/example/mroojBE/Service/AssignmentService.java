package com.example.mroojBE.Service;

import com.example.mroojBE.DTOs.ResponseDTO.AssignmentLogResponseDTO;
import com.example.mroojBE.Entity.AssignmentLog;
import com.example.mroojBE.Entity.Booking;
import com.example.mroojBE.Entity.Consultant;
import com.example.mroojBE.Entity.enums.AssignmentOutcome;
import com.example.mroojBE.Entity.enums.BookingStatus;
import com.example.mroojBE.exceptions.InvalidBookingStateException;
import com.example.mroojBE.exceptions.NoConsultantAvailableException;
import com.example.mroojBE.exceptions.ResourceNotFoundException;
import com.example.mroojBE.repository.AssignmentLogRepository;
import com.example.mroojBE.repository.BookingRepository;
import com.example.mroojBE.repository.ConsultantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {

    // Matches the 100km fallback documented on ConsultantRepository#findNearestAvailableByDomain
    private static final double DEFAULT_SEARCH_RADIUS_METERS = 100_000;

    private final AssignmentLogRepository assignmentLogRepository;
    private final BookingRepository bookingRepository;
    private final ConsultantRepository consultantRepository;
    private final AuthenticatedUserService authenticatedUserService;

    /**
     * Core Phase 4 flow: finds the nearest available consultant matching the
     * booking's domain (ConsultantRepository already orders by distance then
     * current_load), auto-confirms the match, attaches the consultant to the
     * booking and bumps their load. Consultants who already have a logged
     * attempt for this booking are excluded, so a retry after a
     * REJECTED/TIMEOUT never re-offers the same person.
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public AssignmentLogResponseDTO assignBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidBookingStateException(
                    "Booking " + bookingId + " is not PENDING (current status: " + booking.getStatus() + ")");
        }
        // Guard against a duplicate ACCEPTED assignment for the same booking —
        // see AssignmentLogRepository#existsByBookingIdAndOutcome for scope/limits.
        if (assignmentLogRepository.existsByBookingIdAndOutcome(bookingId, AssignmentOutcome.ACCEPTED)) {
            throw new InvalidBookingStateException(
                    "Booking " + bookingId + " already has an active ACCEPTED assignment");
        }

        Set<Long> alreadyAttempted = assignmentLogRepository.findByBookingIdOrderByAssignedAtDesc(bookingId)
                .stream()
                .map(log -> log.getConsultant().getId())
                .collect(Collectors.toSet());


        double lat = booking.getLocation().getY();
        double lng = booking.getLocation().getX();

        List<Consultant> candidates = consultantRepository.findNearestAvailableByDomain(
                booking.getDomain().name(), lng, lat, DEFAULT_SEARCH_RADIUS_METERS);

        Consultant chosen = candidates.stream()
                .filter(c -> !alreadyAttempted.contains(c.getId()))
                .findFirst()
                .orElseThrow(() -> new NoConsultantAvailableException(
                        "No available " + booking.getDomain() + " consultant found near this location"));

        AssignmentLog log = AssignmentLog.builder()
                .booking(booking)
                .consultant(chosen)
                .assignedAt(LocalDateTime.now())
                .respondedAt(LocalDateTime.now())
                .outcome(AssignmentOutcome.ACCEPTED)
                .build();
        assignmentLogRepository.save(log);

        booking.setAssignedConsultant(chosen);
        booking.setStatus(BookingStatus.ASSIGNED);

        consultantRepository.incrementLoad(chosen.getId());
        return toDTO(log, "Booking assigned to " + chosen.getUser().getFirstName() + " " + chosen.getUser().getLastName());
    }

    /**
     * Consultant declines an offered/assigned booking: closes out the
     * ACCEPTED log as REJECTED, frees their load, resets the booking to
     * PENDING and immediately retries assignment against the next-nearest
     * candidate (see AssignmentLogRepository's "at most one ACCEPTED attempt
     * per booking" note — this is the application-level enforcement of that).
     */
    public AssignmentLogResponseDTO rejectAndReassign(Long bookingId) {
        Long consultantId = authenticatedUserService.currentConsultant().getId();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        AssignmentLog activeLog = assignmentLogRepository.findByBookingIdAndOutcome(bookingId, AssignmentOutcome.ACCEPTED)
                .orElseThrow(() -> new ResourceNotFoundException("No active assignment found for booking " + bookingId));

        if (!activeLog.getConsultant().getId().equals(consultantId)) {
            throw new InvalidBookingStateException(
                    "Consultant " + consultantId + " is not the currently assigned consultant for booking " + bookingId);
        }

        activeLog.setOutcome(AssignmentOutcome.REJECTED);
        activeLog.setRespondedAt(LocalDateTime.now());

        consultantRepository.decrementLoad(activeLog.getConsultant().getId());
        booking.setAssignedConsultant(null);
        booking.setStatus(BookingStatus.PENDING);

        return assignBooking(bookingId);
    }

    /**
     * Marks a stale offer as TIMEOUT (e.g. from a scheduled sweep of
     * ACCEPTED-but-unactioned assignments) and reassigns, same as a reject.
     */
    public AssignmentLogResponseDTO timeoutAndReassign(Long bookingId, Long consultantId) {
        AssignmentLog activeLog = assignmentLogRepository.findByBookingIdAndOutcome(bookingId, AssignmentOutcome.ACCEPTED)
                .orElseThrow(() -> new ResourceNotFoundException("No active assignment found for booking " + bookingId));

        if (!activeLog.getConsultant().getId().equals(consultantId)) {
            throw new InvalidBookingStateException(
                    "Consultant " + consultantId + " is not the currently assigned consultant for booking " + bookingId);
        }

        activeLog.setOutcome(AssignmentOutcome.TIMEOUT);
        activeLog.setRespondedAt(LocalDateTime.now());

        consultantRepository.decrementLoad(activeLog.getConsultant().getId());

        Booking booking = activeLog.getBooking();
        booking.setAssignedConsultant(null);
        booking.setStatus(BookingStatus.PENDING);

        return assignBooking(booking.getId());
    }

    @Transactional(readOnly = true)
    public List<AssignmentLogResponseDTO> getHistory(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        String email = com.example.mroojBE.Security.SecurityUtils.currentEmail();
        boolean farmerOwner = booking.getFarmer().getUser().getEmail().equalsIgnoreCase(email);
        boolean assignedConsultant = booking.getAssignedConsultant() != null
                && booking.getAssignedConsultant().getUser().getEmail().equalsIgnoreCase(email);
        if (!farmerOwner && !assignedConsultant) {
            throw new org.springframework.security.access.AccessDeniedException("You cannot view this assignment history");
        }
        return assignmentLogRepository.findByBookingIdOrderByAssignedAtDesc(bookingId)
                .stream().map(log -> toDTO(log, null)).toList();
    }

    private AssignmentLogResponseDTO toDTO(AssignmentLog log, String message) {
        Consultant c = log.getConsultant();
        return AssignmentLogResponseDTO.builder()
                .bookingId(log.getBooking().getId())
                .consultantId(c.getId())
                .consultantName(c.getUser().getFirstName() + " " + c.getUser().getLastName())
                .assignedAt(log.getAssignedAt())
                .outcome(log.getOutcome().name())
                .message(message)
                .respondedAt(log.getRespondedAt())
                .build();
    }
}