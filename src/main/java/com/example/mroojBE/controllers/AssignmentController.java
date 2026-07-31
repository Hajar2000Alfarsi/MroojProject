package com.example.mroojBE.controllers;


import com.example.mroojBE.DTOs.ResponseDTO.AssignmentLogResponseDTO;
import com.example.mroojBE.DTOs.ApiResponse;
import com.example.mroojBE.Service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    /** Manual trigger — normally unnecessary since BookingService already calls this on creation. */
    @PostMapping("/bookings/{bookingId}/assign")
    public ResponseEntity<ApiResponse<AssignmentLogResponseDTO>> assignBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.of("Consultant assigned", assignmentService.assignBooking(bookingId)));
    }

    @PostMapping("/bookings/{bookingId}/reject")
    public ResponseEntity<ApiResponse<AssignmentLogResponseDTO>> rejectAndReassign(
            @PathVariable Long bookingId, @RequestParam Long consultantId) {
        AssignmentLogResponseDTO result = assignmentService.rejectAndReassign(bookingId, consultantId);
        return ResponseEntity.ok(ApiResponse.of("Reassigned to next available consultant", result));
    }

    /** Meant to be called by a scheduled sweep of stale offers — exposed here for manual/admin use too. */
    @PostMapping("/bookings/{bookingId}/timeout")
    public ResponseEntity<ApiResponse<AssignmentLogResponseDTO>> timeoutAndReassign(
            @PathVariable Long bookingId, @RequestParam Long consultantId) {
        AssignmentLogResponseDTO result = assignmentService.timeoutAndReassign(bookingId, consultantId);
        return ResponseEntity.ok(ApiResponse.of("Offer timed out — reassigned", result));
    }

    @GetMapping("/bookings/{bookingId}/history")
    public ResponseEntity<ApiResponse<List<AssignmentLogResponseDTO>>> getHistory(@PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.of(assignmentService.getHistory(bookingId)));
    }
}