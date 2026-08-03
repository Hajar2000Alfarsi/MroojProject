package com.example.mroojBE.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingSummary {


        private Long bookingId;


        // مثال: Tomato / Camel / Palm
        private String problemType;


        // اسم المستشار
        // إذا لم يتم تعيين مستشار سيكون "Not Assigned"
        private String consultantName;


        // PENDING / ASSIGNED / RESOLVED ...
        private String status;


    }


}