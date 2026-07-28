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

    public Farmer toEntity() { // For creating
        User user = User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .role(Role.FARMER)
                .preferredLanguage(preferredLanguage != null ? preferredLanguage : "ar")
                .build();

        return Farmer.builder()
                .user(user)
                .farmName(farmName)
                .farmLocation(GeoUtils.toPoint(latitude, longitude))
                .region(region)
                .farmSizeAcres(farmSizeAcres)
                .cropTypes(cropTypes)
                .bio(bio)
                .build();
    }

    public void applyTo(Farmer farmer) { // For updating
        farmer.setFarmName(farmName);
        farmer.setFarmLocation(GeoUtils.toPoint(latitude, longitude));
        farmer.setRegion(region);
        farmer.setFarmSizeAcres(farmSizeAcres);
        farmer.setCropTypes(cropTypes);
        farmer.setBio(bio);

        User user = farmer.getUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        if (preferredLanguage != null) {
            user.setPreferredLanguage(preferredLanguage);
        }
    }
}
