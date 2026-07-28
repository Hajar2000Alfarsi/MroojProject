package com.example.mroojBE.DTOs.ResponseDTO;

import com.example.mroojBE.Entity.Booking;
import com.example.mroojBE.UtilityHelperClass.GeoUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {
    private Long id;
    private Long farmerId;
    private String farmerName;
    private Long consultantId;
    private String consultantName;

    private String title;
    private String description;
    private String cropType;
    private String issueCategory;
    private String domain;
    private String subjectType;
    private String symptomsImageUrl;
    private String aiReport;
    private Double latitude;
    private Double longitude;
    private String consultantResponse;
    private String status;
    private LocalDateTime createdAt;
    private String rejectionReason;


    public static BookingResponseDTO fromEntity(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingResponseDTO dto = new BookingResponseDTO();

        dto.setId(booking.getId());
        dto.setFarmerId(booking.getFarmer().getId());
        dto.setFarmerName(booking.getFarmer().getUser().getFirstName() + " "
                + booking.getFarmer().getUser().getLastName());

        if (booking.getConsultant() != null) {
            dto.setConsultantId(booking.getConsultant().getId());
            dto.setConsultantName(booking.getConsultant().getUser().getFirstName() + " "
                    + booking.getConsultant().getUser().getLastName());
        }

        dto.setTitle(booking.getTitle());
        dto.setDescription(booking.getDescription());
        dto.setCropType(booking.getCropType());
        dto.setIssueCategory(booking.getIssueCategory());
        dto.setDomain(booking.getDomain().name());
        dto.setSubjectType(booking.getSubjectType());
        dto.setSymptomsImageUrl(booking.getSymptomsImageUrl());
        dto.setAiReport(booking.getAiReport());
        dto.setLatitude(GeoUtils.getLatitude(booking.getLocation()));
        dto.setLongitude(GeoUtils.getLongitude(booking.getLocation()));
        dto.setConsultantResponse(booking.getConsultantResponse());
        dto.setStatus(booking.getStatus().name());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setRejectionReason(booking.getRejectionReason());

        return dto;
    }

    public static List<BookingResponseDTO> fromEntity(List<Booking> bookings) {
        List<BookingResponseDTO> dtos = new ArrayList<>();
        if (bookings != null) {
            for (Booking booking : bookings) {
                dtos.add(fromEntity(booking));
            }
        }
        return dtos;
    }
}
