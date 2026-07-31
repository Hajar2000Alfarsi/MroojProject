package com.example.mroojBE.controllers;


import com.example.mroojBE.DTOs.RequestDTO.ConsultantRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.ConsultantResponseDTO;
import com.example.mroojBE.DTOs.ApiResponse;
import com.example.mroojBE.Entity.enums.Domain;
import com.example.mroojBE.Service.ConsultantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultants")
@RequiredArgsConstructor
public class ConsultantController {

    private final ConsultantService consultantService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ConsultantResponseDTO>> register(@Valid @RequestBody ConsultantRequestDTO request) {
        ConsultantResponseDTO created = consultantService.registerConsultant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("Consultant account created", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConsultantResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(consultantService.getConsultantById(id)));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ApiResponse<ConsultantResponseDTO>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.of(consultantService.getConsultantByUserId(userId)));
    }

    // TODO(PHASE-JWT): {id} should be verified against the authenticated principal
    // once JwtAuthFilter exists, so a consultant can only edit their own profile.
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ConsultantResponseDTO>> updateProfile(
            @PathVariable Long id, @Valid @RequestBody ConsultantRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.of("Profile updated", consultantService.updateConsultantProfile(id, request)));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<ConsultantResponseDTO>> setAvailability(
            @PathVariable Long id, @RequestParam boolean available) {
        String message = available ? "Marked as available" : "Marked as unavailable";
        return ResponseEntity.ok(ApiResponse.of(message, consultantService.setAvailability(id, available)));
    }

    /** Admin/debug preview of who the assignment engine would currently match — doesn't commit anything. */
    @GetMapping("/nearest")
    public ResponseEntity<ApiResponse<List<ConsultantResponseDTO>>> findNearestAvailable(
            @RequestParam Domain domain,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false) Double radiusMeters) {
        List<ConsultantResponseDTO> results = consultantService.findNearestAvailable(domain, lat, lng, radiusMeters);
        return ResponseEntity.ok(ApiResponse.of(results));
    }
}