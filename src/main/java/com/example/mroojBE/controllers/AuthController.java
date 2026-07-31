package com.example.mroojBE.controllers;


import com.example.mroojBE.DTOs.RequestDTO.LoginRequestDTO;
import com.example.mroojBE.DTOs.RequestDTO.RegisterRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.AuthResponseDTO;
import com.example.mroojBE.DTOs.ResponseDTO.UserResponseDTO;
import com.example.mroojBE.DTOs.ApiResponse;
import com.example.mroojBE.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Bare USERS account creation (e.g. ADMIN). Farmers/Consultants should
     * use POST /api/farmers/register or /api/consultants/register instead —
     * those create the profile in the same call.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        UserResponseDTO created = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("Account created", created));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO result = authService.login(request);
        return ResponseEntity.ok(ApiResponse.of("Login successful", result));
    }
}