package com.example.mroojBE.Entity.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingStatusTest {
    @Test
    void containsStatusesRequiredByTheConsultationFlow() {
        assertNotNull(BookingStatus.valueOf("PENDING"));
        assertNotNull(BookingStatus.valueOf("ASSIGNED"));
        assertNotNull(BookingStatus.valueOf("IN_PROGRESS"));
        assertNotNull(BookingStatus.valueOf("RESOLVED"));
        assertNotNull(BookingStatus.valueOf("CANCELLED"));
    }
}
