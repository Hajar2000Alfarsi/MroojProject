package com.example.mroojBE.Service;

import com.example.mroojBE.DTOs.RequestDTO.AppointmentRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.AppointmentResponseDTO;
import com.example.mroojBE.Entity.Appointment;
import com.example.mroojBE.Entity.Booking;
import com.example.mroojBE.Entity.Consultant;
import com.example.mroojBE.Entity.enums.AppointmentStatus;
import com.example.mroojBE.Entity.enums.BookingStatus;
import com.example.mroojBE.exceptions.InvalidBookingStateException;
import com.example.mroojBE.exceptions.ResourceNotFoundException;
import com.example.mroojBE.repository.AppointmentRepository;
import com.example.mroojBE.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
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

    public AppointmentResponseDTO scheduleAppointment(AppointmentRequestDTO request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));

        if (booking.getAssignedConsultant() == null
                || !booking.getAssignedConsultant().getId().equals(request.getConsultantId())) {
            throw new InvalidBookingStateException(
                    "Consultant " + request.getConsultantId() + " is not assigned to booking " + booking.getId());
        }
        if (booking.getStatus() != BookingStatus.ASSIGNED && booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new InvalidBookingStateException(
                    "Booking " + booking.getId() + " must be ASSIGNED or IN_PROGRESS to schedule an appointment");
        }

        Consultant consultant = booking.getAssignedConsultant();
        int duration = request.getDurationMinutes() != null ? request.getDurationMinutes() : 30;

        LocalDateTime windowStart = request.getScheduledAt();
        LocalDateTime windowEnd = windowStart.plusMinutes(duration);

        // Widen the query window by one slot on each side so any appointment
        // whose own span overlaps [windowStart, windowEnd) is caught.
        boolean doubleBooked = !appointmentRepository
                .findOverlapping(consultant.getId(), windowStart, windowEnd)
                .isEmpty();

        if (doubleBooked) {
            throw new InvalidBookingStateException(
                    "Consultant " + consultant.getId() + " already has an appointment overlapping this time slot");
        }
        if (doubleBooked) {
            throw new InvalidBookingStateException(
                    "Consultant " + consultant.getId() + " already has an appointment overlapping this time slot");
        }

        Appointment appointment = Appointment.builder()
                .booking(booking)
                .farmer(booking.getFarmer())
                .consultant(consultant)
                .scheduledAt(windowStart)
                .durationMinutes(duration)
                .status(AppointmentStatus.SCHEDULED)
                .meetingLink(request.getMeetingLink())
                .location(request.getLocation())
                .build();

        return toDTO(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDTO getAppointmentById(Long appointmentId) {
        return toDTO(findOrThrow(appointmentId));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> listByFarmer(Long farmerId) {
        return appointmentRepository.findByFarmerIdOrderByScheduledAtDesc(farmerId)
                .stream().map(this::toDTO).toList();
    }

    /** Calendar view for a consultant's dashboard (see AppointmentRepository docs). */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> listByConsultantCalendar(Long consultantId, LocalDateTime from, LocalDateTime to) {
        return appointmentRepository.findByConsultantIdAndScheduledAtBetween(consultantId, from, to)
                .stream().map(this::toDTO).toList();
    }

    public AppointmentResponseDTO updateStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = findOrThrow(appointmentId);
        appointment.setStatus(status);
        return toDTO(appointment);
    }

    public AppointmentResponseDTO cancelAppointment(Long appointmentId) {
        Appointment appointment = findOrThrow(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidBookingStateException("Cannot cancel a completed appointment");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return toDTO(appointment);
    }

    private Appointment findOrThrow(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
    }

    private AppointmentResponseDTO toDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getBooking().getId(),
                appointment.getFarmer().getId(),
                appointment.getConsultant().getId(),
                appointment.getConsultant().getUser().getFirstName() + " "
                        + appointment.getConsultant().getUser().getLastName(),
                appointment.getScheduledAt(),
                appointment.getScheduledAt().plusMinutes(appointment.getDurationMinutes()),
                appointment.getDurationMinutes(),
                appointment.getStatus().name(),
                appointment.getMeetingLink(),
                appointment.getLocation(),
                null,
                null

        );
    }
}