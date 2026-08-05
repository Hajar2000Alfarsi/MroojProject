package com.example.mroojBE.controllers;

import com.example.mroojBE.DTOs.AI.AiAnalysisResponse;
import com.example.mroojBE.DTOs.ApiResponse;
import com.example.mroojBE.Entity.enums.Domain;
import com.example.mroojBE.Service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AiAnalysisResponse>> analyze(
            @RequestParam Domain domain,
            @RequestParam String subjectType,
            @RequestParam String description,
            @RequestParam MultipartFile image) {

        AiAnalysisResponse result = aiAnalysisService.analyze(domain, subjectType, description, image);
        return ResponseEntity.ok(ApiResponse.of("Preliminary analysis completed", result));
    }
}
