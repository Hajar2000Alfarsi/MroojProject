package com.example.mroojBE.DTOs.RequestDTO;

import com.example.mroojBE.DTOs.LocationDto;
import com.example.mroojBE.Entity.enums.Domain;
//import com.example.mroojBE.UtilityHelperClass.GeoUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookingRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String cropType;

    private String issueCategory;

    @NotNull(message = "Domain is required")
    private Domain domain; // PLANT / LIVESTOCK

    @NotBlank(message = "Subject type is required")
    private String subjectType; // e.g. "tomato", "cattle"
    private String symptomsImageUrl;

    /** Structured JSON returned by /api/ai/analyze; optional when AI is unavailable. */
    private String aiReport;
    @NotNull(message = "Location is required")

    @Valid
    private LocationDto location;
}