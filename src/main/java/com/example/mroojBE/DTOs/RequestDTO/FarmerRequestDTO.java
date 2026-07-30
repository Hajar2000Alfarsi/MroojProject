package com.example.mroojBE.DTOs.RequestDTO;

import com.example.mroojBE.Entity.Farmer;
import com.example.mroojBE.Entity.User;
import com.example.mroojBE.Entity.enums.Role;
import com.example.mroojBE.UtilityHelperClass.GeoUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FarmerRequestDTO {
    // ---- user fields (registration) ----
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

    private String preferredLanguage;

    // ---- farmer fields ----
    @NotBlank(message = "Farm name is required")
    private String farmName;

    @NotNull(message = "Farm latitude is required")
    private Double latitude;

    @NotNull(message = "Farm longitude is required")
    private Double longitude;

    private String region;

    private Double farmSizeAcres;

    private String cropTypes;

    private String bio;
}