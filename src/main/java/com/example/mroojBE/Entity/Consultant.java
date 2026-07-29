package com.example.mroojBE.Entity;

import com.example.mroojBE.Entity.enums.Domain;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * CONSULTANTS entity — profile owned One-to-One by a User with role
 * CONSULTANT. current_load and rating feed the Phase 4 assignment algorithm.
 *
 * ERD notes location as "POINT NOT NULL SRID 4326 + SPATIAL INDEX" — the
 * spatial index is a DB-level concern (schema migration), not expressible
 * through the @Column annotation alone.
 */
@Entity
@Table(name = "consultants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"user", "bookings", "appointments", "assignmentLogs"})
@EqualsAndHashCode(callSuper = false, exclude = {"user", "bookings", "appointments", "assignmentLogs"})
public class Consultant extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "specialty_domain", nullable = false, length = 20)
    private Domain specialtyDomain;

    @Column(name = "specialty_tags", length = 255)
    private String specialtyTags;

    @Column(nullable = false, columnDefinition = "POINT SRID 4326")
    private Point location;

    @Column(name = "current_load", nullable = false)
    @Builder.Default
    private int currentLoad = 0;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(nullable = false)
    @Builder.Default
    private double rating = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private boolean available = true;

    // Inverse side of Booking.assignedConsultant
    @OneToMany(mappedBy = "assignedConsultant", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // Inverse side of Appointment.consultant
    @OneToMany(mappedBy = "consultant", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    // Inverse side of AssignmentLog.consultant
    @OneToMany(mappedBy = "consultant", fetch = FetchType.LAZY)
    @Builder.Default
    private List<AssignmentLog> assignmentLogs = new ArrayList<>();
}