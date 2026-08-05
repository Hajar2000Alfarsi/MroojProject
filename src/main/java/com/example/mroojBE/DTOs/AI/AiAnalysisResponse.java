package com.example.mroojBE.DTOs.AI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisResponse {
    private String summary;
    private String possibleIssue;
    private String confidence;
    private List<String> observations;
    private List<String> recommendedActions;
    private String urgency;
    private List<String> missingInformation;
    private String disclaimer;
    private String imageUrl;
}
