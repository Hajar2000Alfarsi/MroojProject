package com.example.mroojBE.controllers;


import com.example.mroojBE.DTOs.RequestDTO.UserRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.UserResponseDTO;
import com.example.mroojBE.DTOs.ApiResponse;
import com.example.mroojBE.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(userService.getById(id)));
    }

    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.of(userService.getByEmail(email)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateProfile(
            @PathVariable Long id, @Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.of("Profile updated", userService.updateProfile(id, request)));
    }

    /** ADMIN-only in practice — enforce via the role check once JwtAuthFilter exists. */
    @PatchMapping("/{id}/enabled")
    public ResponseEntity<ApiResponse<UserResponseDTO>> setEnabled(
            @PathVariable Long id, @RequestParam boolean enabled) {
        String message = enabled ? "Account enabled" : "Account disabled";
        return ResponseEntity.ok(ApiResponse.of(message, userService.setEnabled(id, enabled)));
    }
}