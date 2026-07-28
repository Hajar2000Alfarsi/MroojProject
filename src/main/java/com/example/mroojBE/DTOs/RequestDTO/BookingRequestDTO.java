package com.example.mroojBE.DTOs.RequestDTO;

import com.example.mroojBE.Entity.Booking;
import com.example.mroojBE.Entity.enums.ConsultationDomain;
import com.example.mroojBE.UtilityHelperClass.GeoUtils;
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
    private ConsultationDomain domain; // PLANT / LIVESTOCK

    private String subjectType;

    private String symptomsImageUrl;

    @NotNull(message = "Location latitude is required")
    private Double latitude;

    @NotNull(message = "Location longitude is required")
    private Double longitude;

    public Booking toEntity() { // For creating
        // farmer is set by the service layer (from the authenticated user)
        return Booking.builder()
                .title(title)
                .description(description)
                .cropType(cropType)
                .issueCategory(issueCategory)
                .domain(domain)
                .subjectType(subjectType)
                .symptomsImageUrl(symptomsImageUrl)
                .location(GeoUtils.toPoint(latitude, longitude))
                .build();
    }

    public void applyTo(Booking booking) { // For updating
        booking.setTitle(title);
        booking.setDescription(description);
        booking.setCropType(cropType);
        booking.setIssueCategory(issueCategory);
        booking.setDomain(domain);
        booking.setSubjectType(subjectType);
        booking.setSymptomsImageUrl(symptomsImageUrl);
        booking.setLocation(GeoUtils.toPoint(latitude, longitude));
    }
}
