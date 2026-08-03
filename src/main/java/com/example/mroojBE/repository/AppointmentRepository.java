package com.example.mroojBE.repository;

import com.example.mroojBE.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByBookingId(Long bookingId);

    List<Appointment> findByFarmerIdOrderByScheduledAtDesc(Long farmerId);

    /**
     * Calendar view for a consultant's dashboard, and also used to check
     * for double-booking before confirming a new appointment slot.
     */
    List<Appointment> findByConsultantIdAndScheduledAtBetween(
            Long consultantId, LocalDateTime from, LocalDateTime to);

    @Query(value = """
            SELECT * FROM appointments a
            WHERE a.consultant_id = :consultantId
              AND a.status <> 'CANCELLED'
              AND a.scheduled_at < :windowEnd
              AND TIMESTAMPADD(MINUTE, a.duration_minutes, a.scheduled_at) > :windowStart
            """, nativeQuery = true)
    List<Appointment> findOverlapping(
            @Param("consultantId") Long consultantId,
            @Param("windowStart") LocalDateTime windowStart,
            @Param("windowEnd") LocalDateTime windowEnd
    );


    // Dashboard
    long countByFarmerIdAndScheduledAtAfter(
            Long farmerId,
            LocalDateTime dateTime
    );
}