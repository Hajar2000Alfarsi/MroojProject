package com.example.mroojBE.DTOs;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FarmerDashboardResponse {


    // Farmer information
    private Long farmerId;

    private String firstName;

    private String lastName;

    private String farmName;


    // Dashboard statistics
    private Long totalRequests;

    private Long pendingRequests;

    private Long resolvedRequests;

    private Long upcomingAppointments;


    // Recent bookings
    private List<BookingSummary> recentBookings;



    @Data
    @Builder
    public static class BookingSummary {


        private Long bookingId;

        private String problemType;

        private String consultantName;

        private String status;

    }

}