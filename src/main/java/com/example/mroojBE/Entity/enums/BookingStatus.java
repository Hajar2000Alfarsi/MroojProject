package com.example.mroojBE.Entity.enums;


/**
 * Lifecycle of a consultation request. Drives BOOKINGS.status.
 */
public enum BookingStatus {
    PENDING,        // created, not yet assigned to a consultant
    ASSIGNED,       // matched to a consultant via the assignment algorithm
    IN_PROGRESS,    // consultant is actively working the case
    RESOLVED,       // consultant_response provided, case closed successfully
    CANCELLED,
    REJECTED    // farmer or admin cancelled the request
}

