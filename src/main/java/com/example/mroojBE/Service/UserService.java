package com.example.mroojBE.Service;

import com.example.mroojBE.DTOs.RequestDTO.UserRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.UserResponseDTO;
import com.example.mroojBE.Entity.User;
import com.example.mroojBE.exceptions.ResourceNotFoundException;
import com.example.mroojBE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponseDTO getById(Long userId) {
        return toDTO(findOrThrow(userId));
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return toDTO(user);
    }

    /**
     * email/password/role intentionally not touched here — UserRequestDTO#applyTo
     * already deliberately skips them; those need dedicated
     * change-password / change-email / admin-only role-change endpoints.
     */
    public UserResponseDTO updateProfile(Long userId, UserRequestDTO request) {
        User user = findOrThrow(userId);
        request.applyTo(user);
        return toDTO(user);
    }

    public UserResponseDTO setEnabled(Long userId, boolean enabled) {
        User user = findOrThrow(userId);
        user.setEnabled(enabled);
        return toDTO(user);
    }

    private User findOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
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