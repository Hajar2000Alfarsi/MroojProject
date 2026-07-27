package com.example.mroojBE.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "farmers")
@Data
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farmer extends BaseEntity {
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 200)
    private String farmName;

    @Column(columnDefinition = "POINT SRID 4326", nullable = false)
    private Point farmLocation;
    @Column(length = 100)
    private String region;

    @Column
    private Double farmSizeAcres;

    @Column(length = 500)
    private String cropTypes;

    @Column(length = 1000)
    private String bio;

    //cascade = CascadeType.ALL --> each change in framer will be also changed in booking
    //orphanRemoval --> any framer booking delete from framer also delete it from DB
    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

}
