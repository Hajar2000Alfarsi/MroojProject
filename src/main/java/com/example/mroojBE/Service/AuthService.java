package com.example.mroojBE.Service;

import com.example.mroojBE.DTOs.RequestDTO.LoginRequestDTO;
import com.example.mroojBE.DTOs.RequestDTO.RegisterRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.AuthResponseDTO;
import com.example.mroojBE.DTOs.ResponseDTO.UserResponseDTO;
import com.example.mroojBE.Entity.User;
import com.example.mroojBE.Service.JwtService;
import com.example.mroojBE.exceptions.DuplicateResourceException;
import com.example.mroojBE.exceptions.ResourceNotFoundException;
import com.example.mroojBE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles bare USERS accounts and login. Farmers and Consultants normally
 * register in one shot via FarmerService#registerFarmer /
 * ConsultantService#registerConsultant, since FarmerRequestDTO /
 * ConsultantRequestDTO already carry both the account fields and the
 * profile fields — RegisterRequestDTO alone can't create a valid Farmer or
 * Consultant profile (no farm/specialty fields on it), so #register() here
 * is really only for ADMIN accounts.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }
        String encoded = passwordEncoder.encode(request.getPassword());
        User saved = userRepository.save(request.toEntity(encoded));
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid email or password");
        }
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("This account has been disabled");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .user(toDTO(user))
                .build();
    }

    private UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole().name(),
                user.isEnabled(),
                user.getPreferredLanguage()
        );
    }
}