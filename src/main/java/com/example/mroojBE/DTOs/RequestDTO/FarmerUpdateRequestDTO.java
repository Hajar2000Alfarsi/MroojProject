package com.example.mroojBE.DTOs.RequestDTO;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class FarmerUpdateRequestDTO {


    private String firstName;


    private String lastName;


    private String phone;


    private String preferredLanguage;



    private String farmName;


    private Double latitude;


    private Double longitude;


    private String region;


    private Double farmSizeAcres;


    private String cropTypes;


    private String bio;


}