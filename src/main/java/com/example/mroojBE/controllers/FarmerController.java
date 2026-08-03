package com.example.mroojBE.controllers;


import com.example.mroojBE.DTOs.RequestDTO.FarmerRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.FarmerResponseDTO;
import com.example.mroojBE.DTOs.ApiResponse;
import com.example.mroojBE.Service.FarmerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.mroojBE.DTOs.FarmerDashboardResponse;

@RestController
@RequestMapping("/api/farmers")
@RequiredArgsConstructor
public class FarmerController {

    private final FarmerService farmerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<FarmerResponseDTO>> register(@Valid @RequestBody FarmerRequestDTO request) {
        FarmerResponseDTO created = farmerService.registerFarmer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("Farmer account created", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FarmerResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(farmerService.getFarmerById(id)));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ApiResponse<FarmerResponseDTO>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.of(farmerService.getFarmerByUserId(userId)));
    }

    // TODO(PHASE-JWT): {id} should be verified against the authenticated principal
    // once JwtAuthFilter exists, so a farmer can only edit their own profile.
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FarmerResponseDTO>> updateProfile(
            @PathVariable Long id, @Valid @RequestBody FarmerRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.of("Profile updated", farmerService.updateFarmerProfile(id, request)));
    }

    //dashboard
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<ApiResponse<FarmerDashboardResponse>> getDashboard(
            @PathVariable Long userId
    ){

        return ResponseEntity.ok(
                ApiResponse.of(
                        farmerService.getFarmerDashboard(userId)
                )
        );

    }
}