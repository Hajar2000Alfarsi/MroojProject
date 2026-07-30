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

}
