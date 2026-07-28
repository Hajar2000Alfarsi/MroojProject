package com.example.mroojBE.DTOs.RequestDTO;

import com.example.mroojBE.Entity.Consultant;
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
public class ConsultantRequestDTO {

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

    @NotBlank(message = "Specialty domain is required")
    private String specialtyDomain; // PLANT / LIVESTOCK

    private String specialtyTags;

    @NotNull(message = "Location latitude is required")
    private Double latitude;

    @NotNull(message = "Location longitude is required")
    private Double longitude;

    private Integer experienceYears;

    private String bio;

    private String qualifications;

    public Consultant toEntity() { // For creating
        User user = User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .role(Role.CONSULTANT)
                .preferredLanguage(preferredLanguage != null ? preferredLanguage : "ar")
                .build();

        return Consultant.builder()
                .user(user)
                .specialtyDomain(specialtyDomain)
                .specialtyTags(specialtyTags)
                .location(GeoUtils.toPoint(latitude, longitude))
                .experienceYears(experienceYears != null ? experienceYears : 0)
                .bio(bio)
                .qualifications(qualifications)
                .build();
    }

    public void applyTo(Consultant consultant) { // For updating
        consultant.setSpecialtyDomain(specialtyDomain);
        consultant.setSpecialtyTags(specialtyTags);
        consultant.setLocation(GeoUtils.toPoint(latitude, longitude));
        consultant.setExperienceYears(experienceYears);
        consultant.setBio(bio);
        consultant.setQualifications(qualifications);

        User user = consultant.getUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        if (preferredLanguage != null) {
            user.setPreferredLanguage(preferredLanguage);
        }
    }

}
