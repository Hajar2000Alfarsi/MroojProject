package com.example.mroojBE.DTOs.RequestDTO;

import com.example.mroojBE.Entity.User;
import com.example.mroojBE.Entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String phone;

    @NotBlank(message = "Role is required")
    private String role; // "FARMER" or "CONSULTANT"

    private String preferredLanguage;

    public User toEntity() { // For creating
        return User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .role(Role.valueOf(role.toUpperCase()))
                .preferredLanguage(preferredLanguage != null ? preferredLanguage : "ar")
                .build();
    }

    public void applyTo(User user) { // For updating
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        if (preferredLanguage != null) {
            user.setPreferredLanguage(preferredLanguage);
        }
        // email, password, role intentionally NOT updated here —
        // those need dedicated endpoints (change-password, change-email, admin-only role change)
    }
}
