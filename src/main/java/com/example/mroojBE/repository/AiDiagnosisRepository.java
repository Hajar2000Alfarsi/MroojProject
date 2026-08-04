package com.example.mroojBE.repository;

import com.example.mroojBE.DTOs.RequestDTO.AiDiagnosisRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.AiDiagnosisResponseDTO;

public interface AiDiagnosisRepository {

    /**
     * Used by AiDiagnosisController — image comes directly from the
     * multipart upload (live preview before the farmer submits the booking).
     */
    AiDiagnosisResponseDTO diagnose(AiDiagnosisRequestDTO request, byte[] imageBytes, String imageMimeType);

    /**
     * Used by BookingService — the image is already referenced by URL
     * (Booking.symptomsImageUrl), so this variant downloads it first.
     * imageUrl may be null/blank for a text-only diagnosis.
     */
    AiDiagnosisResponseDTO diagnoseFromImageUrl(AiDiagnosisRequestDTO request, String imageUrl);
}