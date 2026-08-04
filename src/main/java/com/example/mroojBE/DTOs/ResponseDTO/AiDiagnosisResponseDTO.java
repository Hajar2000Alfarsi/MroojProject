package com.example.mroojBE.DTOs.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Serialized (via ObjectMapper.writeValueAsString) into Booking.aiReport,
 * which is a String column with columnDefinition = "json".
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiDiagnosisResponseDTO {
    private String probableIssue;
    private String urgency;        // LOW | MEDIUM | HIGH
    private String confidence;     // LOW | MEDIUM | HIGH
    private List<String> possibleCauses;
    private List<String> recommendedSteps;
    private String disclaimer;

    // true only when mrooj.ai.gemini.api-key is unset (deliberate dev-mode
    // state) — NOT set on a real call failure, per NOTE-B1 on Booking.java.
    private boolean mock;
}