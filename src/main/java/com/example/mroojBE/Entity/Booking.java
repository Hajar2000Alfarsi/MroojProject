package com.example.mroojBE.Entity;

import com.example.mroojBE.Entity.enums.BookingStatus;
import com.example.mroojBE.Entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.metamodel.model.domain.DomainType;

import java.util.List;
import org.locationtech.jts.geom.Point;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name = "bookings")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {
    //FetchType.LAZY--> download the data when it needed
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(length = 100)
    private String cropType;

    @Column(length = 100)
    private String issueCategory;

    //to take only value that's there in BookingStatus in enum file
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role domain;   // enum: PLANT, LIVESTOCK

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    //

    @Column(length = 1000)
    private String subjectType;

    @Column(length = 1000)
    private String consultantResponse;

    @Column(length = 5000)
    private String symptomsImageUrl;

    @Column(columnDefinition = "json")
    private String aiReport;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point location;

    //

    @Column(length = 1000)
    private String rejectionReason;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Appointment appointment;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();
}
