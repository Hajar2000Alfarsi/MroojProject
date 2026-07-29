package com.example.mroojBE.repository;

import com.example.mroojBE.Entity.LearningArticle;
import com.example.mroojBE.Entity.enums.Domain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LearningArticleRepository extends JpaRepository<LearningArticle, Long> {

    Optional<LearningArticle> findBySlugAndPublishedTrue(String slug);

    Page<LearningArticle> findByTargetDomainAndPublishedTrue(Domain targetDomain, Pageable pageable);

    boolean existsBySlug(String slug);
}