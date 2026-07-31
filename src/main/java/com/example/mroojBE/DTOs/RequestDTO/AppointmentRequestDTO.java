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
}