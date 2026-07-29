package com.example.mroojBE.DTOs.RequestDTO;

import com.example.mroojBE.Entity.User;
import com.example.mroojBE.Entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequestDTO {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String phone;

    @NotBlank(message = "Role is required")
    private String role; // "FARMER" or "CONSULTANT"

    /**
     * encodedPassword must already be BCrypt-hashed by the caller
     * (AuthService) before this is invoked — this DTO never persists
     * the raw password.
     */
    public User toEntity(String encodedPassword) {
        User user = new User();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setPhone(phone);
        user.setRole(Role.valueOf(role.toUpperCase()));
        user.setEnabled(true);
        user.setPreferredLanguage("ar");

        return user;
    }
}
