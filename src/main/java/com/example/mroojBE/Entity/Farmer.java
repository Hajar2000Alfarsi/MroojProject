package com.example.mroojBE.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * FARMERS entity — profile owned One-to-One by a User with role FARMER.
 * Owning side of the User link (holds user_id). Also owns the inverse
 * (mappedBy) side of its Bookings and Appointments for navigation like
 * farmer.getBookings().
 */
@Entity
@Table(name = "farmers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"user", "bookings", "appointments"})
@EqualsAndHashCode(callSuper = false, exclude = {"user", "bookings", "appointments"})
public class Farmer extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "farm_name", nullable = false, length = 150)
    private String farmName;

    /**
     * ERD: "POINT NOT NULL SRID 4326". Requires hibernate-spatial on the
     * classpath for the org.locationtech.jts.geom.Point type to resolve.
     */
    @Column(name = "farm_location", nullable = false, columnDefinition = "POINT SRID 4326")
    private Point farmLocation;

    @Column(length = 100)
    private String region;

    @Column(name = "farm_size_acres")
    private Double farmSizeAcres;

    @Column(name = "crop_types", length = 255)
    private String cropTypes;

    // Inverse side of Booking.farmer — read-only convenience, don't rely
    // on this collection to persist new bookings; save via BookingRepository.
    @OneToMany(mappedBy = "farmer", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // Inverse side of Appointment.farmer
    @OneToMany(mappedBy = "farmer", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();
}