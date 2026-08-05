package com.example.mroojBE.Service;

import com.example.mroojBE.DTOs.RequestDTO.ConsultantRequestDTO;
import com.example.mroojBE.DTOs.RequestDTO.ConsultantUpdateRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.ConsultantResponseDTO;
import com.example.mroojBE.Entity.Consultant;
import com.example.mroojBE.Entity.User;
import com.example.mroojBE.Entity.enums.Domain;
import com.example.mroojBE.Entity.enums.Role;
import com.example.mroojBE.UtilityHelperClass.GeoUtils;
import com.example.mroojBE.exceptions.DuplicateResourceException;
import com.example.mroojBE.exceptions.ResourceNotFoundException;
import com.example.mroojBE.repository.ConsultantRepository;
import com.example.mroojBE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultantService {

    // Matches the 100km fallback documented on ConsultantRepository#findNearestAvailableByDomain
    private static final double DEFAULT_SEARCH_RADIUS_METERS = 100_000;

    private final ConsultantRepository consultantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserService authenticatedUserService;

    public ConsultantResponseDTO registerConsultant(ConsultantRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(Role.CONSULTANT)
                .enabled(true)
                .preferredLanguage(request.getPreferredLanguage() != null ? request.getPreferredLanguage() : "ar")
                .build();
        User savedUser = userRepository.save(user);

        Point location = GeoUtils.createPoint(request.getLatitude(), request.getLongitude());
        Consultant consultant = Consultant.builder()
                .user(savedUser)
                .specialtyDomain(parseDomain(request.getSpecialtyDomain()))
                .specialtyTags(request.getSpecialtyTags())
                .location(location)
                .currentLoad(0)
                .experienceYears(request.getExperienceYears())
                .rating(0.0)
                .available(true)
                .build();
        Consultant saved = consultantRepository.save(consultant);

        return toDTO(saved);
    }


    @Transactional(readOnly = true)
    public ConsultantResponseDTO getMyProfile() {
        return toDTO(authenticatedUserService.currentConsultant());
    }

    public ConsultantResponseDTO updateMyProfile(ConsultantUpdateRequestDTO request) {
        Consultant consultant = authenticatedUserService.currentConsultant();
        User user = consultant.getUser();

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) user.setFirstName(request.getFirstName().trim());
        if (request.getLastName() != null && !request.getLastName().isBlank()) user.setLastName(request.getLastName().trim());
        if (request.getPhone() != null) user.setPhone(request.getPhone().trim());
        if (request.getSpecialtyDomain() != null && !request.getSpecialtyDomain().isBlank()) {
            consultant.setSpecialtyDomain(parseDomain(request.getSpecialtyDomain()));
        }
        if (request.getSpecialtyTags() != null) consultant.setSpecialtyTags(request.getSpecialtyTags().trim());
        if (request.getExperienceYears() != null) consultant.setExperienceYears(request.getExperienceYears());
        if (request.getLatitude() != null && request.getLongitude() != null) {
            consultant.setLocation(GeoUtils.createPoint(request.getLatitude(), request.getLongitude()));
        }
        if (request.getAvailable() != null) consultant.setAvailable(request.getAvailable());

        return toDTO(consultant);
    }

    @Transactional(readOnly = true)
    public ConsultantResponseDTO getConsultantById(Long consultantId) {
        return toDTO(findOrThrow(consultantId));
    }

    @Transactional(readOnly = true)
    public ConsultantResponseDTO getConsultantByUserId(Long userId) {
        Consultant consultant = consultantRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultant profile not found for user id: " + userId));
        return toDTO(consultant);
    }

    public ConsultantResponseDTO updateConsultantProfile(Long consultantId, ConsultantRequestDTO request) {
        Consultant consultant = findOrThrow(consultantId);

        if (request.getSpecialtyDomain() != null) {
            consultant.setSpecialtyDomain(parseDomain(request.getSpecialtyDomain()));
        }
        consultant.setSpecialtyTags(request.getSpecialtyTags());
        consultant.setExperienceYears(request.getExperienceYears());
        if (request.getLatitude() != null && request.getLongitude() != null) {
            consultant.setLocation(GeoUtils.createPoint(request.getLatitude(), request.getLongitude()));
        }

        User user = consultant.getUser();
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        return toDTO(consultant);
    }

    public ConsultantResponseDTO setAvailability(Long consultantId, boolean available) {
        Consultant consultant = findOrThrow(consultantId);
        consultant.setAvailable(available);
        return toDTO(consultant);
    }

    /**
     * Thin wrapper around the native spatial query on ConsultantRepository —
     * exposed for admin/dashboard "who would this booking match to" previews,
     * separate from AssignmentService which actually commits an assignment.
     */
    @Transactional(readOnly = true)
    public List<ConsultantResponseDTO> findNearestAvailable(Domain domain, double lat, double lng, Double radiusMeters) {
        double radius = radiusMeters != null ? radiusMeters : DEFAULT_SEARCH_RADIUS_METERS;
        return consultantRepository.findNearestAvailableByDomain(domain.name(), lng, lat, radius)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private Domain parseDomain(String raw) {
        try {
            return Domain.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("specialtyDomain must be PLANT or LIVESTOCK");
        }
    }

    private Consultant findOrThrow(Long consultantId) {
        return consultantRepository.findById(consultantId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultant not found with id: " + consultantId));
    }

    private ConsultantResponseDTO toDTO(Consultant consultant) {
        User user = consultant.getUser();
        return new ConsultantResponseDTO(
                consultant.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                consultant.getSpecialtyDomain().name(),
                consultant.getSpecialtyTags(),
                GeoUtils.latitudeOf(consultant.getLocation()),
                GeoUtils.longitudeOf(consultant.getLocation()),
                consultant.getCurrentLoad(),
                consultant.getExperienceYears(),
                consultant.getRating(),
                consultant.getBookings() == null ? 0 : consultant.getBookings().size(),
                consultant.isAvailable()
        );
    }
}