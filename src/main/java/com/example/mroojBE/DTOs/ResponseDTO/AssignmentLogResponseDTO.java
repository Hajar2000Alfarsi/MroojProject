package com.example.mroojBE.DTOs.ResponseDTO;

import com.example.mroojBE.Entity.AssignmentLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentLogResponseDTO {

    private Long id;
    private Long bookingId;
    private Long consultantId;
    private String consultantName;
    private LocalDateTime assignedAt;
    private LocalDateTime respondedAt;
    private String outcome;

    public static AssignmentLogResponseDTO fromEntity(AssignmentLog log) {
        if (log == null) {
            return null;
        }

        AssignmentLogResponseDTO dto = new AssignmentLogResponseDTO();

        dto.setId(log.getId());
        dto.setBookingId(log.getBooking().getId());
        dto.setConsultantId(log.getConsultant().getId());
        dto.setConsultantName(log.getConsultant().getUser().getFirstName() + " "
                + log.getConsultant().getUser().getLastName());
        dto.setAssignedAt(log.getAssignedAt());
        dto.setRespondedAt(log.getRespondedAt());
        dto.setOutcome(log.getOutcome());

        return dto;
    }

    public static List<AssignmentLogResponseDTO> fromEntity(List<AssignmentLog> logs) {
        List<AssignmentLogResponseDTO> dtos = new ArrayList<>();
        if (logs != null) {
            for (AssignmentLog log : logs) {
                dtos.add(fromEntity(log));
            }
        }
        return dtos;
    }
}