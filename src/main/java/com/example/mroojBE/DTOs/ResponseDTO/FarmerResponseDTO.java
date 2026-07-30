package com.example.mroojBE.DTOs.ResponseDTO;

import com.example.mroojBE.Entity.Farmer;
//import com.example.mroojBE.UtilityHelperClass.GeoUtils;
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

}
