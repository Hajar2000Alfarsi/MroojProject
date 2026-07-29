package com.example.mroojBE.Entity;

import com.example.mroojBE.Entity.enums.Domain;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * LEARNING_ARTICLES entity — the standalone knowledge hub (Phase 5).
 * No FK to USERS/CONSULTANTS: target_domain and author_name are plain
 * tags/free text per the ERD, so articles stay manageable independently
 * of any specific account.
 */
@Entity
@Table(name = "learning_articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class LearningArticle extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String title;

    // URL-friendly unique identifier, e.g. "citrus-leaf-miner-treatment".
    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 100)
    private String category;

    // NEW field — PLANT or LIVESTOCK, no FK per ERD.
    @Enumerated(EnumType.STRING)
    @Column(name = "target_domain", nullable = false, length = 20)
    private Domain targetDomain;

    // NEW field (i18n) — ISO language code. Defaults to Arabic.
    @Column(nullable = false, length = 5)
    @Builder.Default
    private String language = "ar";

    // NEW field — free-text author, intentionally not an FK to USERS so
    // external contributors/agencies can be credited too.
    @Column(name = "author_name", length = 100)
    private String authorName;

    @Column(nullable = false)
    @Builder.Default
    private boolean published = false;

    // Set explicitly by the service layer at the moment of publishing —
    // distinct from createdAt, since a draft may exist long before that.
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}