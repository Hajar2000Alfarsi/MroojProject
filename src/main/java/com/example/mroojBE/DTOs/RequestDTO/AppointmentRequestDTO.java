package com.example.mroojBE.DTOs.RequestDTO;

import com.example.mroojBE.Entity.Appointment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AppointmentRequestDTO {

    @NotNull(message = "Booking id is required")
    private Long bookingId;

    private Long consultantId; // ignored; consultant identity is derived from JWT

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledAt;

    private Integer durationMinutes;

    private String meetingLink;

    private String location;

    private String notes;
}