package com.example.mroojBE.DTOs.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearningArticleResponseDTO {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String category;
    private String targetDomain;
    private String language;
    private String authorName;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}