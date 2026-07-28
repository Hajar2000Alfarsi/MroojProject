package com.example.mroojBE.DTOs.ResponseDTO;

import com.example.mroojBE.Entity.Consultant;
import com.example.mroojBE.UtilityHelperClass.GeoUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultantResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    private String specialtyDomain;
    private String specialtyTags;
    private Double latitude;
    private Double longitude;
    private Integer currentLoad;
    private Integer experienceYears;
    private Double rating;
    private Integer totalReviews;
    private Boolean available;

    public static ConsultantResponseDTO fromEntity(Consultant consultant) {
        if (consultant == null) {
            return null;
        }

        ConsultantResponseDTO dto = new ConsultantResponseDTO();

        dto.setId(consultant.getId());
        dto.setEmail(consultant.getUser().getEmail());
        dto.setFirstName(consultant.getUser().getFirstName());
        dto.setLastName(consultant.getUser().getLastName());
        dto.setPhone(consultant.getUser().getPhone());

        dto.setSpecialtyDomain(consultant.getSpecialtyDomain());
        dto.setSpecialtyTags(consultant.getSpecialtyTags());
        dto.setLatitude(GeoUtils.getLatitude(consultant.getLocation()));
        dto.setLongitude(GeoUtils.getLongitude(consultant.getLocation()));
        dto.setCurrentLoad(consultant.getCurrentLoad());
        dto.setExperienceYears(consultant.getExperienceYears());
        dto.setRating(consultant.getRating());
        dto.setTotalReviews(consultant.getTotalReviews());
        dto.setAvailable(consultant.getAvailable());

        return dto;
    }

    public static List<ConsultantResponseDTO> fromEntity(List<Consultant> consultants) {
        List<ConsultantResponseDTO> dtos = new ArrayList<>();
        if (consultants != null) {
            for (Consultant consultant : consultants) {
                dtos.add(fromEntity(consultant));
            }
        }
        return dtos;
    }
}
