package com.example.mroojBE.controllers;


import com.example.mroojBE.DTOs.RequestDTO.BookingRequestDTO;
import com.example.mroojBE.DTOs.RequestDTO.BookingResolveRequest;
import com.example.mroojBE.DTOs.ResponseDTO.BookingResponseDTO;
import com.example.mroojBE.DTOs.ApiResponse;
import com.example.mroojBE.Entity.enums.BookingStatus;
import com.example.mroojBE.Entity.enums.Domain;
import com.example.mroojBE.Service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * NOTE: farmerId/consultantId are taken as explicit request params/path
 * segments rather than pulled from SecurityContextHolder, since no
 * JwtAuthFilter is registered yet (see SecurityConfig's TODO(PHASE-JWT)).
 * Swap these for the authenticated principal once that filter exists.
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @RequestParam Long farmerId, @Valid @RequestBody BookingRequestDTO request) {
        BookingResponseDTO created = bookingService.createBooking(farmerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("Booking created", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(bookingService.getBookingById(id)));
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<ApiResponse<Page<BookingResponseDTO>>> listByFarmer(
            @PathVariable Long farmerId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(bookingService.listByFarmer(farmerId, pageable)));
    }

    @GetMapping("/consultant/{consultantId}")
    public ResponseEntity<ApiResponse<Page<BookingResponseDTO>>> listByConsultant(
            @PathVariable Long consultantId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(bookingService.listByConsultant(consultantId, pageable)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<BookingResponseDTO>>> listByStatus(
            @PathVariable BookingStatus status, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(bookingService.listByStatus(status, pageable)));
    }

    @GetMapping("/domain/{domain}/status/{status}")
    public ResponseEntity<ApiResponse<Page<BookingResponseDTO>>> listByDomainAndStatus(
            @PathVariable Domain domain, @PathVariable BookingStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(bookingService.listByDomainAndStatus(domain, status, pageable)));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> startProgress(
            @PathVariable Long id, @RequestParam Long consultantId) {
        return ResponseEntity.ok(ApiResponse.of("Booking moved to IN_PROGRESS", bookingService.startProgress(id, consultantId)));
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> resolveBooking(
            @PathVariable Long id, @RequestParam Long consultantId,
            @Valid @RequestBody BookingResolveRequest request) {
        return ResponseEntity.ok(ApiResponse.of("Booking resolved", bookingService.resolveBooking(id, consultantId, request)));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBooking(
            @PathVariable Long id, @RequestParam Long farmerId, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(ApiResponse.of("Booking cancelled", bookingService.cancelBooking(id, farmerId, reason)));
    }

    /** ADMIN-only in practice — enforce via the role check once JwtAuthFilter exists. */
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> adminRejectBooking(
            @PathVariable Long id, @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.of("Booking rejected", bookingService.adminRejectBooking(id, reason)));
    }
}