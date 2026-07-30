package com.example.mroojBE.Service;

import com.example.mroojBE.DTOs.LocationDto;
import com.example.mroojBE.DTOs.RequestDTO.BookingRequestDTO;
import com.example.mroojBE.DTOs.RequestDTO.BookingResolveRequest;
import com.example.mroojBE.DTOs.ResponseDTO.BookingResponseDTO;
import com.example.mroojBE.Entity.Booking;
import com.example.mroojBE.Entity.Consultant;
import com.example.mroojBE.Entity.Farmer;
import com.example.mroojBE.Entity.enums.BookingStatus;
import com.example.mroojBE.Entity.enums.Domain;
import com.example.mroojBE.UtilityHelperClass.GeoUtils;
import com.example.mroojBE.exceptions.InvalidBookingStateException;
import com.example.mroojBE.exceptions.NoConsultantAvailableException;
import com.example.mroojBE.exceptions.ResourceNotFoundException;
import com.example.mroojBE.repository.BookingRepository;
import com.example.mroojBE.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

        private static final Set<BookingStatus> CANCELLABLE = EnumSet.of(
                BookingStatus.PENDING, BookingStatus.ASSIGNED, BookingStatus.IN_PROGRESS);

        private final BookingRepository bookingRepository;
        private final FarmerRepository farmerRepository;
        private final AssignmentService assignmentService;

        /**
         * Persists the booking as PENDING, then immediately attempts
         * auto-assignment. Per NOTE-B1 on the entity ("downstream failures are
         * never silently swallowed"), only the one expected business outcome —
         * "no consultant available right now" — is caught here and leaves the
         * booking PENDING for later retry; any other exception from the
         * assignment step propagates and rolls back with the booking creation.
         */
        public BookingResponseDTO createBooking(Long farmerId, BookingRequestDTO request) {
                Farmer farmer = farmerRepository.findById(farmerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with id: " + farmerId));

                Booking booking = Booking.builder()
                        .farmer(farmer)
                        .domain(request.getDomain())
                        .subjectType(request.getSubjectType())
                        .issueCategory(request.getIssueCategory())
                        .description(request.getDescription())
                        .symptomsImageUrl(request.getSymptomsImageUrl())
                        .location(GeoUtils.createPoint(request.getLocation().getLatitude(), request.getLocation().getLongitude()))
                        .status(BookingStatus.PENDING)
                        .build();
                Booking saved = bookingRepository.save(booking);

                try {
                        assignmentService.assignBooking(saved.getId());
                } catch (NoConsultantAvailableException ex) {
                        // Expected outcome — booking stays PENDING for later retry/admin follow-up.
                }

                return toDTO(saved);
        }

        @Transactional(readOnly = true)
        public BookingResponseDTO getBookingById(Long bookingId) {
                return toDTO(findOrThrow(bookingId));
        }

        @Transactional(readOnly = true)
        public Page<BookingResponseDTO> listByFarmer(Long farmerId, Pageable pageable) {
                return bookingRepository.findByFarmerId(farmerId, pageable).map(this::toDTO);
        }

        @Transactional(readOnly = true)
        public Page<BookingResponseDTO> listByConsultant(Long consultantId, Pageable pageable) {
                return bookingRepository.findByAssignedConsultantId(consultantId, pageable).map(this::toDTO);
        }

        @Transactional(readOnly = true)
        public Page<BookingResponseDTO> listByStatus(BookingStatus status, Pageable pageable) {
                return bookingRepository.findByStatus(status, pageable).map(this::toDTO);
        }

        @Transactional(readOnly = true)
        public Page<BookingResponseDTO> listByDomainAndStatus(Domain domain, BookingStatus status, Pageable pageable) {
                return bookingRepository.findByDomainAndStatus(domain, status, pageable).map(this::toDTO);
        }

        public BookingResponseDTO startProgress(Long bookingId, Long consultantId) {
                Booking booking = findOrThrow(bookingId);
                assertAssignedConsultant(booking, consultantId);
                if (booking.getStatus() != BookingStatus.ASSIGNED) {
                        throw new InvalidBookingStateException(
                                "Booking " + bookingId + " must be ASSIGNED to start progress (current: " + booking.getStatus() + ")");
                }
                booking.setStatus(BookingStatus.IN_PROGRESS);
                return toDTO(booking);
        }

        public BookingResponseDTO resolveBooking(Long bookingId, Long consultantId, BookingResolveRequest request) {
                Booking booking = findOrThrow(bookingId);
                assertAssignedConsultant(booking, consultantId);
                if (booking.getStatus() != BookingStatus.ASSIGNED && booking.getStatus() != BookingStatus.IN_PROGRESS) {
                        throw new InvalidBookingStateException(
                                "Booking " + bookingId + " cannot be resolved from status " + booking.getStatus());
                }
                booking.setConsultantResponse(request.getConsultantResponse());
                booking.setStatus(BookingStatus.RESOLVED);
                return toDTO(booking);
        }

        public BookingResponseDTO cancelBooking(Long bookingId, Long farmerId, String reason) {
                Booking booking = findOrThrow(bookingId);
                if (!booking.getFarmer().getId().equals(farmerId)) {
                        throw new InvalidBookingStateException("Booking " + bookingId + " does not belong to farmer " + farmerId);
                }
                if (!CANCELLABLE.contains(booking.getStatus())) {
                        throw new InvalidBookingStateException(
                                "Booking " + bookingId + " cannot be cancelled from status " + booking.getStatus());
                }
                if (booking.getAssignedConsultant() != null) {
                        Consultant consultant = booking.getAssignedConsultant();
                        consultant.setCurrentLoad(Math.max(0, consultant.getCurrentLoad() - 1));
                }
                booking.setConsultantResponse(reason);
                booking.setStatus(BookingStatus.CANCELLED);
                return toDTO(booking);
        }

        /** Admin-only: rejects a still-PENDING booking as invalid/out-of-scope. */
        public BookingResponseDTO adminRejectBooking(Long bookingId, String reason) {
                Booking booking = findOrThrow(bookingId);
                if (booking.getStatus() != BookingStatus.PENDING) {
                        throw new InvalidBookingStateException(
                                "Only PENDING bookings can be administratively rejected (current: " + booking.getStatus() + ")");
                }
                booking.setConsultantResponse(reason);
                booking.setStatus(BookingStatus.REJECTED);
                return toDTO(booking);
        }

        private void assertAssignedConsultant(Booking booking, Long consultantId) {
                if (booking.getAssignedConsultant() == null || !booking.getAssignedConsultant().getId().equals(consultantId)) {
                        throw new InvalidBookingStateException(
                                "Consultant " + consultantId + " is not assigned to booking " + booking.getId());
                }
        }

        private Booking findOrThrow(Long bookingId) {
                return bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        }

        private BookingResponseDTO toDTO(Booking booking) {
                Consultant consultant = booking.getAssignedConsultant();
                boolean terminalNegative = booking.getStatus() == BookingStatus.CANCELLED
                        || booking.getStatus() == BookingStatus.REJECTED;

                return BookingResponseDTO.builder()
                        .id(booking.getId())
                        .farmerId(booking.getFarmer().getId())
                        .farmerName(booking.getFarmer().getUser().getFirstName() + " " + booking.getFarmer().getUser().getLastName())
                        .consultantId(consultant != null ? consultant.getId() : null)
                        .consultantName(consultant != null
                                ? consultant.getUser().getFirstName() + " " + consultant.getUser().getLastName()
                                : null)
                        .domain(booking.getDomain().name())
                        .subjectType(booking.getSubjectType())
                        .issueCategory(booking.getIssueCategory())
                        .description(booking.getDescription())
                        .symptomsImageUrl(booking.getSymptomsImageUrl())
                        .aiReport(booking.getAiReport())
                        .location(new LocationDto(GeoUtils.latitudeOf(booking.getLocation()), GeoUtils.longitudeOf(booking.getLocation())))
                        .consultantResponse(booking.getConsultantResponse())
                        .status(booking.getStatus().name())
                        .createdAt(booking.getCreatedAt())
                        .rejectionReason(terminalNegative ? booking.getConsultantResponse() : null)
                        .build();
        }
}