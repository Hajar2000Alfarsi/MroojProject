package com.example.mroojBE.DTOs.ResponseDTO;

import com.example.mroojBE.Entity.Consultant;
//import com.example.mroojBE.UtilityHelperClass.GeoUtils;
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

}
