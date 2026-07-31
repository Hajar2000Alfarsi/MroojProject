package com.example.mroojBE.controllers;


import com.example.mroojBE.DTOs.RequestDTO.LearningArticleRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.LearningArticleResponseDTO;
import com.example.mroojBE.DTOs.ApiResponse;
import com.example.mroojBE.Entity.enums.Domain;
import com.example.mroojBE.Service.LearningArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class LearningArticleController {

    private final LearningArticleService learningArticleService;

    /** ADMIN-only in practice — enforce via the role check once JwtAuthFilter exists. */
    @PostMapping
    public ResponseEntity<ApiResponse<LearningArticleResponseDTO>> createArticle(
            @Valid @RequestBody LearningArticleRequestDTO request) {
        LearningArticleResponseDTO created = learningArticleService.createArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("Article created as draft", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LearningArticleResponseDTO>> updateArticle(
            @PathVariable Long id, @Valid @RequestBody LearningArticleRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.of("Article updated", learningArticleService.updateArticle(id, request)));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<LearningArticleResponseDTO>> publish(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of("Article published", learningArticleService.publishArticle(id)));
    }

    @PatchMapping("/{id}/unpublish")
    public ResponseEntity<ApiResponse<LearningArticleResponseDTO>> unpublish(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of("Article unpublished", learningArticleService.unpublishArticle(id)));
    }

    /** Public — only ever returns published articles. */
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<LearningArticleResponseDTO>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.of(learningArticleService.getPublishedBySlug(slug)));
    }

    /** Public — only ever returns published articles. */
    @GetMapping("/domain/{domain}")
    public ResponseEntity<ApiResponse<Page<LearningArticleResponseDTO>>> listByDomain(
            @PathVariable Domain domain, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(learningArticleService.listPublishedByDomain(domain, pageable)));
    }
}