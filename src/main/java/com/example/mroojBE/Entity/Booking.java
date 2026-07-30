package com.example.mroojBE.Entity;

import com.example.mroojBE.Entity.enums.BookingStatus;
import com.example.mroojBE.Entity.enums.Domain;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * BOOKINGS entity — the central consultation request.
 *
 * NOTE-B1 (architectural rule): non-2xx responses from any downstream call
 * (AI wrapper, assignment service) are always surfaced as error paths in
 * the service layer — never silently swallowed or replaced with mock data.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"farmer", "assignedConsultant", "appointments", "assignmentLogs"})
@EqualsAndHashCode(callSuper = false, exclude = {"farmer", "assignedConsultant", "appointments", "assignmentLogs"})
public class Booking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    // Nullable until Phase 4's assignment algorithm matches a consultant.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_consultant_id")
    private Consultant assignedConsultant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Domain domain;

    @Column(name = "subject_type", nullable = false, length = 100)
    private String subjectType;

    @Column(name = "issue_category", length = 100)
    private String issueCategory;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "symptoms_image_url", length = 500)
    private String symptomsImageUrl;

    /**
     * Raw JSON from the AI diagnosis wrapper (Gemini API), kept as String
     * for now; swap for a mapped POJO via a converter if structured
     * querying is needed later.
     */
    @Column(name = "ai_report", columnDefinition = "json")
    private String aiReport;

    @Column(nullable = false, columnDefinition = "POINT SRID 4326")
    private Point location;

    // NEW field — the consultant's treatment plan once resolved.
    @Column(name = "consultant_response", columnDefinition = "TEXT")
    private String consultantResponse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    // Inverse side of Appointment.booking
    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    // Inverse side of AssignmentLog.booking
    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    @Builder.Default
    private List<AssignmentLog> assignmentLogs = new ArrayList<>();
}