package com.example.mroojBE.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Shared base for every entity: id + audit timestamps.
 *
 * IMPORTANT: because subclasses use Lombok's @Builder, this class (and
 * every subclass) must use @SuperBuilder instead — plain @Builder on a
 * subclass silently ignores fields declared in the superclass, so
 * id/createdAt/updatedAt would never make it into a built object.
 * @SuperBuilder fixes that by chaining the builder through the hierarchy.
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}