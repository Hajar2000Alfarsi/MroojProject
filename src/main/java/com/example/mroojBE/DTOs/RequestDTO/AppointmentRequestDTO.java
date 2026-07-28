package com.example.mroojBE.DTOs.RequestDTO;

import com.example.mroojBE.Entity.Appointment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AppointmentRequestDTO {

    @NotNull(message = "Booking id is required")
    private Long bookingId;

    @NotNull(message = "Consultant id is required")
    private Long consultantId;

    @NotNull(message = "Scheduled time is required")
    private LocalDateTime scheduledAt;

    private Integer durationMinutes;

    private String meetingLink;

    private String location;

    private String notes;

    public Appointment toEntity() { // For creating
        // booking, farmer, consultant are resolved and set by the service layer
        return Appointment.builder()
                .scheduledAt(scheduledAt)
                .endAt(scheduledAt.plusMinutes(durationMinutes != null ? durationMinutes : 60))
                .durationMinutes(durationMinutes != null ? durationMinutes : 60)
                .meetingLink(meetingLink)
                .location(location)
                .notes(notes)
                .build();
    }

    public void applyTo(Appointment appointment) { // For updating
        appointment.setScheduledAt(scheduledAt);
        appointment.setEndAt(scheduledAt.plusMinutes(durationMinutes != null ? durationMinutes : 60));
        appointment.setDurationMinutes(durationMinutes);
        appointment.setMeetingLink(meetingLink);
        appointment.setLocation(location);
        appointment.setNotes(notes);
    }
}
