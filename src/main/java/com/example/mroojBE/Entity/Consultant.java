package com.example.mroojBE.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultants")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultant extends BaseEntity {

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 50)
    private String specialtyDomain;   // PLANT / LIVESTOCK

    @Column(length = 500)
    private String specialtyTags;     // "tomatoes,pests,irrigation"


    //
    @Column(nullable = false)
    @Builder.Default
    private Integer currentLoad = 0;

    @Column(columnDefinition = "POINT SRID 4326", nullable = false)
    private Point location;   //
    //
    @Column(nullable = false)
    @Builder.Default
    private Integer experienceYears = 0;

    @Column(length = 1000)
    private String bio;

    @Column(length = 255)
    private String qualifications;

    @Column
    @Builder.Default
    private Double rating = 0.0;

    @Column
    @Builder.Default
    private Integer totalReviews = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;

    @OneToMany(mappedBy = "consultant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "consultant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "consultant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AssignmentLog> assignmentLogs = new ArrayList<>();
}
