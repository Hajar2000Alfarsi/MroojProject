package com.example.mroojBE.DTOs.RequestDTO;

import com.example.mroojBE.Entity.enums.Domain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NOTE (audit finding): this DTO didn't exist yet — LearningArticle had no
 * Request/Response DTOs at all despite having an Entity + Repository.
 */
@Data
@NoArgsConstructor
public class LearningArticleRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String category;

    @NotNull(message = "Target domain is required")
    private Domain targetDomain; // PLANT / LIVESTOCK

    private String language;

    private String authorName;
}