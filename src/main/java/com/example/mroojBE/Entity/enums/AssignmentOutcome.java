package com.example.mroojBE.Entity.enums;

/**
 * Result of a single assignment attempt logged in ASSIGNMENT_LOG.
 */
public enum AssignmentOutcome {
    PENDING,    // offered to the consultant, awaiting response
    ACCEPTED,
    REJECTED,
    TIMEOUT     // consultant did not respond in time, reassigned elsewhere
}