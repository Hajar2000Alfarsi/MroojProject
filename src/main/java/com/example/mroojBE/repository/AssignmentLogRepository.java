package com.example.mroojBE.repository;

import com.example.mroojBE.Entity.AssignmentLog;
import com.example.mroojBE.Entity.enums.AssignmentOutcome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentLogRepository extends JpaRepository<AssignmentLog, Long> {

    List<AssignmentLog> findByBookingIdOrderByAssignedAtDesc(Long bookingId);

    /**
     * Enforces "at most one ACCEPTED attempt per booking" at the query
     * level so the assignment service can check before writing.
     * A DB-level guarantee for this still needs a unique constraint or
     * application-level transactional check — see architecture notes.
     */
    Optional<AssignmentLog> findByBookingIdAndOutcome(Long bookingId, AssignmentOutcome outcome);

    List<AssignmentLog> findByConsultantIdAndOutcome(Long consultantId, AssignmentOutcome outcome);
    boolean existsByBookingIdAndOutcome(Long bookingId, AssignmentOutcome outcome);

}