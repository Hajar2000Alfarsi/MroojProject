package com.example.mroojBE.Entity;

import com.example.mroojBE.Entity.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * APPOINTMENTS entity — a scheduled live session tied to a Booking.
 * farmer_id / consultant_id are denormalized alongside booking_id (per
 * ERD) so calendar-style queries don't always need to join through Booking.
 */
@Entity
@Table(name = "appointments")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"booking", "farmer", "consultant"})
@EqualsAndHashCode(callSuper = false, exclude = {"booking", "farmer", "consultant"})
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultant consultant;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "duration_minutes", nullable = false)
    @Builder.Default
    private int durationMinutes = 30;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Column(name = "meeting_link", length = 500)
    private String meetingLink;

    /**
     * Free-text location note (e.g. "on-site visit, north gate") — distinct
     * from the geo Point on Farmer/Consultant.
     */
    @Column(length = 255)
    private String location;
}