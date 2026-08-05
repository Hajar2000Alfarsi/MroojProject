package com.example.mroojBE.controllers;

import com.example.mroojBE.DTOs.ApiResponse;
import com.example.mroojBE.DTOs.ResponseDTO.AssignmentLogResponseDTO;
import com.example.mroojBE.Service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @PostMapping("/bookings/{bookingId}/reject")
    @PreAuthorize("hasRole('CONSULTANT')")
    public ResponseEntity<ApiResponse<AssignmentLogResponseDTO>> rejectAndReassign(@PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.of("Assignment rejected and reassignment attempted",
                assignmentService.rejectAndReassign(bookingId)));
    }

    @GetMapping("/bookings/{bookingId}/history")
    public ResponseEntity<ApiResponse<List<AssignmentLogResponseDTO>>> history(@PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.of(assignmentService.getHistory(bookingId)));
    }
}
