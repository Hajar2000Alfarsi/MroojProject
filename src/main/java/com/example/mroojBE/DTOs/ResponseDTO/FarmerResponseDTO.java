package com.example.mroojBE.DTOs.ResponseDTO;

import com.example.mroojBE.Entity.Farmer;
import com.example.mroojBE.UtilityHelperClass.GeoUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FarmerResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    private String farmName;
    private Double latitude;
    private Double longitude;
    private String region;
    private Double farmSizeAcres;
    private String cropTypes;
    private String bio;

    public static FarmerResponseDTO fromEntity(Farmer farmer) {
        if (farmer == null) {
            return null;
        }

        FarmerResponseDTO dto = new FarmerResponseDTO();

        dto.setId(farmer.getId());
        dto.setEmail(farmer.getUser().getEmail());
        dto.setFirstName(farmer.getUser().getFirstName());
        dto.setLastName(farmer.getUser().getLastName());
        dto.setPhone(farmer.getUser().getPhone());

        dto.setFarmName(farmer.getFarmName());
        dto.setLatitude(GeoUtils.getLatitude(farmer.getFarmLocation()));
        dto.setLongitude(GeoUtils.getLongitude(farmer.getFarmLocation()));
        dto.setRegion(farmer.getRegion());
        dto.setFarmSizeAcres(farmer.getFarmSizeAcres());
        dto.setCropTypes(farmer.getCropTypes());
        dto.setBio(farmer.getBio());

        return dto;
    }

    public static List<FarmerResponseDTO> fromEntity(List<Farmer> farmers) {
        List<FarmerResponseDTO> dtos = new ArrayList<>();
        if (farmers != null) {
            for (Farmer farmer : farmers) {
                dtos.add(fromEntity(farmer));
            }
        }
        return dtos;
    }
}
