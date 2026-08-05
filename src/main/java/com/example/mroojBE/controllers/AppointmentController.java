package com.example.mroojBE.controllers;

import com.example.mroojBE.DTOs.ApiResponse;
import com.example.mroojBE.DTOs.RequestDTO.AppointmentRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.AppointmentResponseDTO;
import com.example.mroojBE.Entity.enums.AppointmentStatus;
import com.example.mroojBE.Service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('CONSULTANT')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> schedule(@Valid @RequestBody AppointmentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("Appointment scheduled", appointmentService.scheduleAppointment(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(appointmentService.getAuthorizedAppointment(id)));
    }

    @GetMapping("/my/farmer")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDTO>>> myFarmer() {
        return ResponseEntity.ok(ApiResponse.of(appointmentService.listMyFarmerAppointments()));
    }

    @GetMapping("/my/consultant")
    @PreAuthorize("hasRole('CONSULTANT')")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDTO>>> myConsultant(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(ApiResponse.of(appointmentService.listMyConsultantAppointments(from, to)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('CONSULTANT')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> status(
            @PathVariable Long id, @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(ApiResponse.of("Appointment status updated", appointmentService.updateStatus(id, status)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> cancel(
            @PathVariable Long id, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(ApiResponse.of("Appointment cancelled", appointmentService.cancelAppointment(id, reason)));
    }
}
