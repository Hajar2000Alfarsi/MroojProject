package com.example.mroojBE.controllers;

import com.example.mroojBE.DTOs.RequestDTO.AiDiagnosisRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.AiDiagnosisResponseDTO;
import com.example.mroojBE.repository.AiDiagnosisRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * NOTE: the original file this replaces had @CrossOrigin(origins = "*"),
 * which was removed — it conflicts with CorsConfig's
 * allowedOrigins=http://localhost:4200 + allowCredentials(true); browsers
 * reject a wildcard origin combined with credentials. This controller now
 * relies on the same global CorsConfig every other controller uses.
 */
@RestController
@RequestMapping("/api/v1/farmer/ai")
@RequiredArgsConstructor
public class AiDiagnosisController {

    private final AiDiagnosisRepository aiDiagnosisService;

    /**
     * Live preview before the farmer submits the actual booking — the
     * Angular "AI Diagnosis Card" on /farmer/bookings/new calls this
     * directly with the raw image file, no prior upload step needed.
     */
    @PostMapping(value = "/diagnose", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AiDiagnosisResponseDTO> generateDiagnosis(
            @Valid @RequestPart("data") AiDiagnosisRequestDTO requestDto,
            @RequestPart(value = "image", required = false) MultipartFile symptomImage) throws IOException {

        byte[] imageBytes = null;
        String mimeType = null;
        if (symptomImage != null && !symptomImage.isEmpty()) {
            imageBytes = symptomImage.getBytes();
            mimeType = symptomImage.getContentType();
        }

        AiDiagnosisResponseDTO response = aiDiagnosisService.diagnose(requestDto, imageBytes, mimeType);
        return ResponseEntity.ok(response);
    }
}