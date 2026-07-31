package com.example.mroojBE.controllers;


import com.example.mroojBE.DTOs.RequestDTO.AppointmentRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.AppointmentResponseDTO;
import com.example.mroojBE.DTOs.ApiResponse;
import com.example.mroojBE.Entity.enums.AppointmentStatus;
import com.example.mroojBE.Service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> scheduleAppointment(
            @Valid @RequestBody AppointmentRequestDTO request) {
        AppointmentResponseDTO created = appointmentService.scheduleAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("Appointment scheduled", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(appointmentService.getAppointmentById(id)));
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDTO>>> listByFarmer(@PathVariable Long farmerId) {
        return ResponseEntity.ok(ApiResponse.of(appointmentService.listByFarmer(farmerId)));
    }

    /** Calendar view for a consultant's dashboard. */
    @GetMapping("/consultant/{consultantId}/calendar")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDTO>>> listByConsultantCalendar(
            @PathVariable Long consultantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(ApiResponse.of(appointmentService.listByConsultantCalendar(consultantId, from, to)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> updateStatus(
            @PathVariable Long id, @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(ApiResponse.of("Status updated", appointmentService.updateStatus(id, status)));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of("Appointment cancelled", appointmentService.cancelAppointment(id)));
    }
}