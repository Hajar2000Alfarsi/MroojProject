package com.example.mroojBE.Entity;

import com.example.mroojBE.Entity.enums.AssignmentOutcome;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * ASSIGNMENT_LOG entity — audit trail of every attempt the automatic
 * expert-assignment algorithm (Phase 4) makes to match a Booking to a
 * Consultant.
 *
 * Deliberately does NOT extend BaseEntity: the ERD for this table only
 * has (id, booking_id, consultant_id, assigned_at, responded_at, outcome)
 * — no created_at/updated_at columns. Extending BaseEntity would silently
 * add two columns the schema doesn't define. assigned_at is the meaningful
 * "created" moment here and is set explicitly by the assignment service.
 */
@Entity
@Table(name = "assignment_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(exclude = {"booking", "consultant"})
@EqualsAndHashCode(exclude = {"booking", "consultant"})
public class AssignmentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultant consultant;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    // Null until the consultant accepts, rejects, or the offer times out.
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AssignmentOutcome outcome = AssignmentOutcome.PENDING;
}