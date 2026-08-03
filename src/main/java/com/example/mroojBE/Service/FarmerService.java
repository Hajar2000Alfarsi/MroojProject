package com.example.mroojBE.Service;

import com.example.mroojBE.DTOs.RequestDTO.FarmerRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.FarmerResponseDTO;
import com.example.mroojBE.Entity.Farmer;
import com.example.mroojBE.Entity.User;
import com.example.mroojBE.Entity.enums.Role;
import com.example.mroojBE.UtilityHelperClass.GeoUtils;
import com.example.mroojBE.exceptions.DuplicateResourceException;
import com.example.mroojBE.exceptions.ResourceNotFoundException;
import com.example.mroojBE.repository.BookingRepository;
import com.example.mroojBE.repository.FarmerRepository;
import com.example.mroojBE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.mroojBE.DTOs.FarmerDashboardResponse;
import com.example.mroojBE.repository.AppointmentRepository;
import com.example.mroojBE.Entity.enums.BookingStatus;

import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class FarmerService {

    private final FarmerRepository farmerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final BookingRepository bookingRepository;
    private final AppointmentRepository appointmentRepository;

    /**
     * One-shot registration: FarmerRequestDTO carries both the USERS fields
     * and the FARMERS profile fields, so a single call creates both rows in
     * one transaction.
     */
    public FarmerResponseDTO registerFarmer(FarmerRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(Role.FARMER)
                .enabled(true)
                .preferredLanguage(request.getPreferredLanguage() != null ? request.getPreferredLanguage() : "ar")
                .build();
        User savedUser = userRepository.save(user);

        Point location = GeoUtils.createPoint(request.getLatitude(), request.getLongitude());
        Farmer farmer = Farmer.builder()
                .user(savedUser)
                .farmName(request.getFarmName())
                .farmLocation(location)
                .region(request.getRegion())
                .farmSizeAcres(request.getFarmSizeAcres())
                .cropTypes(request.getCropTypes())
                .build();
        Farmer savedFarmer = farmerRepository.save(farmer);

        return toDTO(savedFarmer);
    }

    @Transactional(readOnly = true)
    public FarmerResponseDTO getFarmerById(Long farmerId) {
        return toDTO(findOrThrow(farmerId));
    }

    @Transactional(readOnly = true)
    public FarmerResponseDTO getFarmerByUserId(Long userId) {
        Farmer farmer = farmerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer profile not found for user id: " + userId));
        return toDTO(farmer);
    }

    /**
     * Updates only the FARMERS-owned fields plus the mutable USERS fields
     * (first/last name, phone) — same pattern UserRequestDTO#applyTo follows,
     * email/password stay untouched here.
     */
    public FarmerResponseDTO updateFarmerProfile(Long farmerId, FarmerRequestDTO request) {
        Farmer farmer = findOrThrow(farmerId);

        farmer.setFarmName(request.getFarmName());
        farmer.setRegion(request.getRegion());
        farmer.setFarmSizeAcres(request.getFarmSizeAcres());
        farmer.setCropTypes(request.getCropTypes());
        if (request.getLatitude() != null && request.getLongitude() != null) {
            farmer.setFarmLocation(GeoUtils.createPoint(request.getLatitude(), request.getLongitude()));
        }

        User user = farmer.getUser();
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        return toDTO(farmer);
    }

    private Farmer findOrThrow(Long farmerId) {
        return farmerRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with id: " + farmerId));
    }

    private FarmerResponseDTO toDTO(Farmer farmer) {
        User user = farmer.getUser();
        return new FarmerResponseDTO(
                farmer.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                farmer.getFarmName(),
                GeoUtils.latitudeOf(farmer.getFarmLocation()),
                GeoUtils.longitudeOf(farmer.getFarmLocation()),
                farmer.getRegion(),
                farmer.getFarmSizeAcres(),
                farmer.getCropTypes()
        );
    }


    @Transactional(readOnly = true)
    public FarmerDashboardResponse getFarmerDashboard(Long userId) {


        Farmer farmer = farmerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Farmer profile not found"
                        )
                );


        Long farmerId = farmer.getId();



        long totalRequests =
                bookingRepository.countByFarmerId(farmerId);



        long pendingRequests =
                bookingRepository.countByFarmerIdAndStatus(
                        farmerId,
                        BookingStatus.PENDING
                );



        long resolvedRequests =
                bookingRepository.countByFarmerIdAndStatus(
                        farmerId,
                        BookingStatus.RESOLVED
                );



        long upcomingAppointments =
                appointmentRepository.countByFarmerIdAndScheduledAtAfter(
                        farmerId,
                        LocalDateTime.now()
                );



        return FarmerDashboardResponse.builder()

                .farmName(farmer.getFarmName())

                .totalRequests(totalRequests)

                .pendingRequests(pendingRequests)

                .resolvedRequests(resolvedRequests)

                .upcomingAppointments(upcomingAppointments)

                .build();

    }
}