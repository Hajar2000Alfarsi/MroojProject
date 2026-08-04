package com.example.mroojBE.DTOs.RequestDTO;

import com.example.mroojBE.Entity.enums.Domain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for POST /api/v1/farmer/ai/diagnose (multipart "data" part),
 * and also built internally by BookingService when a farmer submits a
 * new booking, so the AI report can be generated in the same flow.
 */
@Data
@NoArgsConstructor
public class AiDiagnosisRequestDTO {

    @NotNull(message = "Domain is required")
    private Domain domain; // PLANT / LIVESTOCK

    @NotBlank(message = "Subject type is required")
    private String subjectType;

    private String issueCategory;

    @NotBlank(message = "Description is required")
    private String description;

    // "ar" or "en" — BookingService fills this from farmer.getUser().getPreferredLanguage()
    private String preferredLanguage;
}