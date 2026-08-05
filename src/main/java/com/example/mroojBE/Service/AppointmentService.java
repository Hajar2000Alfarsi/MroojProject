package com.example.mroojBE.Service;

import com.example.mroojBE.DTOs.RequestDTO.AppointmentRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.AppointmentResponseDTO;
import com.example.mroojBE.Entity.Appointment;
import com.example.mroojBE.Entity.Booking;
import com.example.mroojBE.Entity.Consultant;
import com.example.mroojBE.Entity.Farmer;
import com.example.mroojBE.Entity.enums.AppointmentStatus;
import com.example.mroojBE.Entity.enums.BookingStatus;
import com.example.mroojBE.exceptions.InvalidBookingStateException;
import com.example.mroojBE.exceptions.ResourceNotFoundException;
import com.example.mroojBE.repository.AppointmentRepository;
import com.example.mroojBE.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final BookingRepository bookingRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public AppointmentResponseDTO scheduleAppointment(AppointmentRequestDTO request) {
        Consultant current = authenticatedUserService.currentConsultant();
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));

        if (booking.getAssignedConsultant() == null || !booking.getAssignedConsultant().getId().equals(current.getId())) {
            throw new AccessDeniedException("Only the assigned consultant can schedule this appointment");
        }
        if (booking.getStatus() != BookingStatus.ASSIGNED
                && booking.getStatus() != BookingStatus.IN_PROGRESS
                && booking.getStatus() != BookingStatus.RESOLVED) {
            throw new InvalidBookingStateException(
                    "Booking must be ASSIGNED, IN_PROGRESS, or RESOLVED before scheduling");
        }
        if (request.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new InvalidBookingStateException("Appointment time must be in the future");
        }
        if (appointmentRepository.findByBookingId(booking.getId()).isPresent()) {
            throw new InvalidBookingStateException("This booking already has an appointment");
        }

        int duration = request.getDurationMinutes() == null ? 30 : request.getDurationMinutes();
        if (duration < 15 || duration > 180) {
            throw new InvalidBookingStateException("Duration must be between 15 and 180 minutes");
        }
        LocalDateTime end = request.getScheduledAt().plusMinutes(duration);
        if (!appointmentRepository.findOverlapping(current.getId(), request.getScheduledAt(), end).isEmpty()) {
            throw new InvalidBookingStateException("The consultant already has an overlapping appointment");
        }

        Appointment appointment = Appointment.builder()
                .booking(booking)
                .farmer(booking.getFarmer())
                .consultant(current)
                .scheduledAt(request.getScheduledAt())
                .durationMinutes(duration)
                .status(AppointmentStatus.SCHEDULED)
                .meetingLink(request.getMeetingLink())
                .location(request.getLocation())
                .notes(request.getNotes())
                .build();
        return toDTO(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDTO getAuthorizedAppointment(Long id) {
        Appointment appointment = findOrThrow(id);
        assertParticipant(appointment);
        return toDTO(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> listMyFarmerAppointments() {
        Farmer farmer = authenticatedUserService.currentFarmer();
        return appointmentRepository.findByFarmerIdOrderByScheduledAtDesc(farmer.getId()).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> listMyConsultantAppointments(LocalDateTime from, LocalDateTime to) {
        Consultant consultant = authenticatedUserService.currentConsultant();
        return appointmentRepository.findByConsultantIdAndScheduledAtBetween(consultant.getId(), from, to)
                .stream().map(this::toDTO).toList();
    }

    public AppointmentResponseDTO updateStatus(Long id, AppointmentStatus status) {
        Consultant consultant = authenticatedUserService.currentConsultant();
        Appointment appointment = findOrThrow(id);
        if (!appointment.getConsultant().getId().equals(consultant.getId())) {
            throw new AccessDeniedException("Only this appointment's consultant can update its status");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidBookingStateException("A finished appointment cannot change status");
        }
        appointment.setStatus(status);
        return toDTO(appointment);
    }

    public AppointmentResponseDTO cancelAppointment(Long id, String reason) {
        Appointment appointment = findOrThrow(id);
        assertParticipant(appointment);
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidBookingStateException("A completed appointment cannot be cancelled");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        return toDTO(appointment);
    }

    private void assertParticipant(Appointment appointment) {
        String email = com.example.mroojBE.Security.SecurityUtils.currentEmail();
        if (!appointment.getFarmer().getUser().getEmail().equalsIgnoreCase(email)
                && !appointment.getConsultant().getUser().getEmail().equalsIgnoreCase(email)) {
            throw new AccessDeniedException("You do not have access to this appointment");
        }
    }

    private Appointment findOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }

    private AppointmentResponseDTO toDTO(Appointment a) {
        return new AppointmentResponseDTO(a.getId(), a.getBooking().getId(), a.getFarmer().getId(),
                a.getConsultant().getId(), a.getConsultant().getUser().getFirstName() + " " + a.getConsultant().getUser().getLastName(),
                a.getScheduledAt(), a.getScheduledAt().plusMinutes(a.getDurationMinutes()), a.getDurationMinutes(),
                a.getStatus().name(), a.getMeetingLink(), a.getLocation(), a.getNotes(), a.getCancellationReason());
    }
}
