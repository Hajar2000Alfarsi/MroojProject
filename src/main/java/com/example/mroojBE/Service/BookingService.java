package com.example.mroojBE.Service;

import com.example.mroojBE.DTOs.LocationDto;
import com.example.mroojBE.DTOs.RequestDTO.BookingRequestDTO;
import com.example.mroojBE.DTOs.RequestDTO.BookingResolveRequest;
import com.example.mroojBE.DTOs.ResponseDTO.BookingResponseDTO;
import com.example.mroojBE.Entity.Booking;
import com.example.mroojBE.Entity.Consultant;
import com.example.mroojBE.Entity.Farmer;
import com.example.mroojBE.Entity.enums.BookingStatus;
import com.example.mroojBE.UtilityHelperClass.GeoUtils;
import com.example.mroojBE.exceptions.InvalidBookingStateException;
import com.example.mroojBE.exceptions.NoConsultantAvailableException;
import com.example.mroojBE.exceptions.ResourceNotFoundException;
import com.example.mroojBE.repository.BookingRepository;
import com.example.mroojBE.repository.ConsultantRepository;
import com.example.mroojBE.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private static final Set<BookingStatus> CANCELLABLE = EnumSet.of(
            BookingStatus.PENDING, BookingStatus.ASSIGNED, BookingStatus.IN_PROGRESS);

    private final BookingRepository bookingRepository;
    private final AssignmentService assignmentService;
    private final ConsultantRepository consultantRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final AppointmentRepository appointmentRepository;

    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        Farmer farmer = authenticatedUserService.currentFarmer();

        Booking booking = Booking.builder()
                .farmer(farmer)
                .domain(request.getDomain())
                .subjectType(request.getSubjectType())
                .issueCategory(request.getIssueCategory())
                .description(request.getDescription())
                .symptomsImageUrl(request.getSymptomsImageUrl())
                .aiReport(request.getAiReport())
                .location(GeoUtils.createPoint(
                        request.getLocation().getLatitude(),
                        request.getLocation().getLongitude()))
                .status(BookingStatus.PENDING)
                .build();

        Booking saved = bookingRepository.saveAndFlush(booking);
        try {
            assignmentService.assignBooking(saved.getId());
        } catch (NoConsultantAvailableException ignored) {
            // Valid business outcome: keep the request pending for a later retry.
        } catch (RuntimeException assignmentFailure) {
            // Assignment must never prevent the farmer request from being saved.
            // The booking stays PENDING and can be assigned later.
        }
        Booking latest = bookingRepository.findById(saved.getId()).orElse(saved);
        return toDTO(latest);
    }

    @Transactional(readOnly = true)
    public BookingResponseDTO getAuthorizedBooking(Long bookingId) {
        Booking booking = findOrThrow(bookingId);
        assertCanView(booking);
        return toDTO(booking);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponseDTO> listMyFarmerBookings(Pageable pageable) {
        return bookingRepository.findByFarmerId(authenticatedUserService.currentFarmer().getId(), pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponseDTO> listMyConsultantBookings(Pageable pageable) {
        return bookingRepository.findByAssignedConsultantId(authenticatedUserService.currentConsultant().getId(), pageable)
                .map(this::toDTO);
    }

    public BookingResponseDTO startProgress(Long bookingId) {
        Consultant consultant = authenticatedUserService.currentConsultant();
        Booking booking = findOrThrow(bookingId);
        assertAssignedConsultant(booking, consultant.getId());
        if (booking.getStatus() != BookingStatus.ASSIGNED) {
            throw new InvalidBookingStateException("Only an ASSIGNED booking can be started");
        }
        booking.setStatus(BookingStatus.IN_PROGRESS);
        return toDTO(booking);
    }

    public BookingResponseDTO resolveBooking(Long bookingId, BookingResolveRequest request) {
        Consultant consultant = authenticatedUserService.currentConsultant();
        Booking booking = findOrThrow(bookingId);
        assertAssignedConsultant(booking, consultant.getId());
        if (booking.getStatus() != BookingStatus.ASSIGNED
                && booking.getStatus() != BookingStatus.IN_PROGRESS
                && booking.getStatus() != BookingStatus.RESOLVED) {
            throw new InvalidBookingStateException("This booking cannot receive a consultant response from status " + booking.getStatus());
        }

        boolean firstResolution = booking.getStatus() != BookingStatus.RESOLVED;
        booking.setConsultantResponse(request.getConsultantResponse());
        booking.setStatus(BookingStatus.RESOLVED);

        // Decrease the consultant load only once. A consultant may edit an
        // already-saved response without decreasing the load again.
        if (firstResolution) {
            consultantRepository.decrementLoad(consultant.getId());
        }
        return toDTO(booking);
    }

    public BookingResponseDTO cancelMyBooking(Long bookingId, String reason) {
        Farmer farmer = authenticatedUserService.currentFarmer();
        Booking booking = findOrThrow(bookingId);
        if (!booking.getFarmer().getId().equals(farmer.getId())) {
            throw new AccessDeniedException("You cannot cancel another farmer's booking");
        }
        if (!CANCELLABLE.contains(booking.getStatus())) {
            throw new InvalidBookingStateException("This booking cannot be cancelled from status " + booking.getStatus());
        }
        if (booking.getAssignedConsultant() != null) {
            consultantRepository.decrementLoad(booking.getAssignedConsultant().getId());
        }
        booking.setConsultantResponse(reason);
        booking.setStatus(BookingStatus.CANCELLED);
        return toDTO(booking);
    }

    private void assertCanView(Booking booking) {
        String email = com.example.mroojBE.Security.SecurityUtils.currentEmail();
        boolean farmerOwner = booking.getFarmer().getUser().getEmail().equalsIgnoreCase(email);
        boolean assignedConsultant = booking.getAssignedConsultant() != null
                && booking.getAssignedConsultant().getUser().getEmail().equalsIgnoreCase(email);
        if (!farmerOwner && !assignedConsultant) {
            throw new AccessDeniedException("You do not have access to this booking");
        }
    }

    private void assertAssignedConsultant(Booking booking, Long consultantId) {
        if (booking.getAssignedConsultant() == null
                || !booking.getAssignedConsultant().getId().equals(consultantId)) {
            throw new AccessDeniedException("You are not assigned to this booking");
        }
    }

    private Booking findOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }

    private BookingResponseDTO toDTO(Booking booking) {
        Consultant consultant = booking.getAssignedConsultant();
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .farmerId(booking.getFarmer().getId())
                .farmerName(booking.getFarmer().getUser().getFirstName() + " " + booking.getFarmer().getUser().getLastName())
                .farmerPhone(booking.getFarmer().getUser().getPhone())
                .consultantId(consultant == null ? null : consultant.getId())
                .consultantName(consultant == null ? null : consultant.getUser().getFirstName() + " " + consultant.getUser().getLastName())
                .consultantPhone(consultant == null ? null : consultant.getUser().getPhone())
                .meetingLink(appointmentRepository.findByBookingId(booking.getId())
                        .map(appointment -> appointment.getMeetingLink())
                        .orElse(null))
                .domain(booking.getDomain().name())
                .subjectType(booking.getSubjectType())
                .issueCategory(booking.getIssueCategory())
                .description(booking.getDescription())
                .symptomsImageUrl(booking.getSymptomsImageUrl())
                .aiReport(booking.getAiReport())
                .location(new LocationDto(booking.getLocation().getY(), booking.getLocation().getX()))
                .consultantResponse(booking.getConsultantResponse())
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
