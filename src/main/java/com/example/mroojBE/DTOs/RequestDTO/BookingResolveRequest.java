package com.example.mroojBE.DTOs.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for CONSULTANT-side resolution of a booking:
 * PUT /bookings/{id}/resolve
 * Sets Booking.consultantResponse and flips status to RESOLVED.
 */
@Data
@NoArgsConstructor
public class BookingResolveRequest {

    @NotBlank(message = "Consultant response (treatment plan) is required")
    private String consultantResponse;
}