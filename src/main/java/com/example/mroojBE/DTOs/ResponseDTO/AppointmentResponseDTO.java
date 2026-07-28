package com.example.mroojBE.DTOs.ResponseDTO;

import com.example.mroojBE.Entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {
    private Long id;
    private Long bookingId;
    private Long farmerId;
    private Long consultantId;
    private String consultantName;
    private LocalDateTime scheduledAt;
    private LocalDateTime endAt;
    private Integer durationMinutes;
    private String status;
    private String meetingLink;
    private String location;
    private String notes;
    private String cancellationReason;

    public static AppointmentResponseDTO fromEntity(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        AppointmentResponseDTO dto = new AppointmentResponseDTO();

        dto.setId(appointment.getId());
        dto.setBookingId(appointment.getBooking().getId());
        dto.setFarmerId(appointment.getFarmer().getId());
        dto.setConsultantId(appointment.getConsultant().getId());
        dto.setConsultantName(appointment.getConsultant().getUser().getFirstName() + " "
                + appointment.getConsultant().getUser().getLastName());
        dto.setScheduledAt(appointment.getScheduledAt());
        dto.setEndAt(appointment.getEndAt());
        dto.setDurationMinutes(appointment.getDurationMinutes());
        dto.setStatus(appointment.getStatus().name());
        dto.setMeetingLink(appointment.getMeetingLink());
        dto.setLocation(appointment.getLocation());
        dto.setNotes(appointment.getNotes());
        dto.setCancellationReason(appointment.getCancellationReason());

        return dto;
    }

    public static List<AppointmentResponseDTO> fromEntity(List<Appointment> appointments) {
        List<AppointmentResponseDTO> dtos = new ArrayList<>();
        if (appointments != null) {
            for (Appointment appointment : appointments) {
                dtos.add(fromEntity(appointment));
            }
        }
        return dtos;
    }
}
