package com.example.mroojBE.DTOs.ResponseDTO;

import com.example.mroojBE.DTOs.LocationDto;
import com.example.mroojBE.Entity.Booking;
//import com.example.mroojBE.UtilityHelperClass.GeoUtils;
import com.example.mroojBE.Entity.enums.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {
    private Long id;

    private Long farmerId;
    private String farmerName;
    private String farmerPhone;


    private Long consultantId;
    private String consultantName;
    private String consultantPhone;
    private String meetingLink;

    private String domain;
    private String subjectType;
    private String issueCategory;
    private String description;
    private String symptomsImageUrl;
    private String aiReport;

    private String cropType;

    private LocationDto location;

    private String consultantResponse;
    private String status;

    private LocalDateTime createdAt;
    private String rejectionReason;


}
