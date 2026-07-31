package com.example.mroojBE.Service;

import com.example.mroojBE.DTOs.RequestDTO.LearningArticleRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.LearningArticleResponseDTO;
import com.example.mroojBE.Entity.LearningArticle;
import com.example.mroojBE.Entity.enums.Domain;
import com.example.mroojBE.exceptions.DuplicateResourceException;
import com.example.mroojBE.exceptions.ResourceNotFoundException;
import com.example.mroojBE.repository.LearningArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class LearningArticleService {

    private static final Pattern NON_SLUG_CHARS = Pattern.compile("[^a-z0-9\\s-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private final LearningArticleRepository learningArticleRepository;

    public LearningArticleResponseDTO createArticle(LearningArticleRequestDTO request) {
        String slug = slugify(request.getTitle());
        if (learningArticleRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("An article with slug '" + slug + "' already exists");
        }

        LearningArticle article = LearningArticle.builder()
                .title(request.getTitle())
                .slug(slug)
                .content(request.getContent())
                .category(request.getCategory())
                .targetDomain(request.getTargetDomain())
                .language(request.getLanguage() != null ? request.getLanguage() : "ar")
                .authorName(request.getAuthorName())
                .published(false)
                .build();

        return toDTO(learningArticleRepository.save(article));
    }

    public LearningArticleResponseDTO updateArticle(Long articleId, LearningArticleRequestDTO request) {
        LearningArticle article = findOrThrow(articleId);
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setCategory(request.getCategory());
        article.setTargetDomain(request.getTargetDomain());
        if (request.getLanguage() != null) article.setLanguage(request.getLanguage());
        article.setAuthorName(request.getAuthorName());
        return toDTO(article);
    }

    public LearningArticleResponseDTO publishArticle(Long articleId) {
        LearningArticle article = findOrThrow(articleId);
        article.setPublished(true);
        article.setPublishedAt(LocalDateTime.now());
        return toDTO(article);
    }

    public LearningArticleResponseDTO unpublishArticle(Long articleId) {
        LearningArticle article = findOrThrow(articleId);
        article.setPublished(false);
        return toDTO(article);
    }

    @Transactional(readOnly = true)
    public LearningArticleResponseDTO getPublishedBySlug(String slug) {
        LearningArticle article = learningArticleRepository.findBySlugAndPublishedTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Published article not found with slug: " + slug));
        return toDTO(article);
    }

    @Transactional(readOnly = true)
    public Page<LearningArticleResponseDTO> listPublishedByDomain(Domain domain, Pageable pageable) {
        return learningArticleRepository.findByTargetDomainAndPublishedTrue(domain, pageable).map(this::toDTO);
    }

    private LearningArticle findOrThrow(Long articleId) {
        return learningArticleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
    }

    private String slugify(String title) {
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD).toLowerCase(Locale.ROOT);
        String noAccents = normalized.replaceAll("\\p{M}", "");
        String cleaned = NON_SLUG_CHARS.matcher(noAccents).replaceAll("").trim();
        String base = WHITESPACE.matcher(cleaned).replaceAll("-");
        return base.isBlank() ? "article-" + System.currentTimeMillis() : base;
    }

    private LearningArticleResponseDTO toDTO(LearningArticle article) {
        return LearningArticleResponseDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .slug(article.getSlug())
                .content(article.getContent())
                .category(article.getCategory())
                .targetDomain(article.getTargetDomain().name())
                .language(article.getLanguage())
                .authorName(article.getAuthorName())
                .published(article.isPublished())
                .publishedAt(article.getPublishedAt())
                .createdAt(article.getCreatedAt())
                .build();
    }
}