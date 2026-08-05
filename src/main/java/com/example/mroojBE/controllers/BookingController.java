package com.example.mroojBE.controllers;

import com.example.mroojBE.DTOs.ApiResponse;
import com.example.mroojBE.DTOs.RequestDTO.BookingRequestDTO;
import com.example.mroojBE.DTOs.RequestDTO.BookingResolveRequest;
import com.example.mroojBE.DTOs.ResponseDTO.BookingResponseDTO;
import com.example.mroojBE.Service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @Valid @RequestBody BookingRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("Booking created", bookingService.createBooking(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(bookingService.getAuthorizedBooking(id)));
    }

    @GetMapping("/my/farmer")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ApiResponse<Page<BookingResponseDTO>>> myFarmerBookings(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(bookingService.listMyFarmerBookings(pageable)));
    }

    @GetMapping("/my/consultant")
    @PreAuthorize("hasRole('CONSULTANT')")
    public ResponseEntity<ApiResponse<Page<BookingResponseDTO>>> myConsultantBookings(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(bookingService.listMyConsultantBookings(pageable)));
    }

    @PatchMapping("/{id}/start")
    @PreAuthorize("hasRole('CONSULTANT')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> startProgress(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of("Booking moved to IN_PROGRESS", bookingService.startProgress(id)));
    }

    @PatchMapping("/{id}/consultant-response")
    @PreAuthorize("hasRole('CONSULTANT')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> resolveBooking(
            @PathVariable Long id, @Valid @RequestBody BookingResolveRequest request) {
        return ResponseEntity.ok(ApiResponse.of("Consultant response saved", bookingService.resolveBooking(id, request)));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBooking(
            @PathVariable Long id, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(ApiResponse.of("Booking cancelled", bookingService.cancelMyBooking(id, reason)));
    }
}
