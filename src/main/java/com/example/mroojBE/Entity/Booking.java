package com.example.mroojBE.Entity;

import com.example.mroojBE.Entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {
    //FetchType.LAZY--> download the data when it needed
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consultant_id", nullable = false)
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
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Column(length = 1000)
    private String consultantResponse;

    @Column(length = 1000)
    private String rejectionReason;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Appointment appointment;

}
