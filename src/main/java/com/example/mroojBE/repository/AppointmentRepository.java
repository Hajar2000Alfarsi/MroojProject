package com.example.mroojBE.repository;

import com.example.mroojBE.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

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
}