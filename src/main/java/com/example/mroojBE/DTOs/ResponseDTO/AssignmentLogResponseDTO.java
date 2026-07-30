package com.example.mroojBE.DTOs.ResponseDTO;

import com.example.mroojBE.Entity.AssignmentLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentLogResponseDTO {
    // No Request DTO for AssignmentLog — this entity is created automatically by the
    // auto-assignment service logic (booking -> best consultant match)

    //private Long id;
    private Long bookingId;
    private Long consultantId;
    private String consultantName;
    private LocalDateTime assignedAt;
    private String outcome;
    private String message;
    private LocalDateTime respondedAt;

}