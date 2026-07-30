package com.example.mroojBE.DTOs;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Shared lat/lng shape used by every Request/Response DTO that wraps a
 * JTS Point. Kept flat (no nested "geometry" object) to match how the
 * Angular frontend already consumes plain latitude/longitude numbers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double longitude;
}