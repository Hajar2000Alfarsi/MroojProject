package com.example.mroojBE.DTOs;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentCreateDTO {
    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Appointment time must be in the future")
    private LocalDateTime scheduledAt;

    private int durationMinutes = 30;
    private String meetingLink;
    private String location;
}