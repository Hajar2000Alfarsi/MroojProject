package com.example.mroojBE.Service;

import com.example.mroojBE.DTOs.AI.AiAnalysisResponse;
import com.example.mroojBE.Entity.enums.Domain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class AiAnalysisService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp"
    );
    private static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;

    private final Path uploadDirectory;

    public AiAnalysisService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadDirectory = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public AiAnalysisResponse analyze(
            Domain domain,
            String subjectType,
            String description,
            MultipartFile image) {

        validateText(subjectType, description);
        String imageUrl = storeImage(image);

        String lower = description.toLowerCase(Locale.ROOT);
        boolean urgentSignal = containsAny(lower,
                "dying", "dead", "cannot breathe", "bleeding", "poison",
                "يموت", "نفوق", "لا يتنفس", "نزيف", "تسمم");

        String possibleIssue = domain == Domain.PLANT
                ? "Possible plant disease, pest, nutrient deficiency, or environmental stress"
                : "Possible livestock illness, injury, nutrition issue, or environmental stress";

        return AiAnalysisResponse.builder()
                .summary("Preliminary analysis prepared from the supplied description and image.")
                .possibleIssue(possibleIssue)
                .confidence("LOW")
                .observations(List.of(
                        "Subject: " + subjectType.trim(),
                        "The description was received successfully.",
                        "The image was stored for consultant review."
                ))
                .recommendedActions(urgentSignal
                        ? List.of("Contact a qualified consultant urgently.",
                                  "Keep the affected plant or animal isolated when safe to do so.",
                                  "Do not apply an unknown treatment before expert review.")
                        : List.of("Monitor changes and record when symptoms started.",
                                  "Prepare additional clear photos if possible.",
                                  "Submit the result to a qualified consultant for confirmation."))
                .urgency(urgentSignal ? "URGENT" : "SOON")
                .missingInformation(List.of(
                        "When did the symptoms begin?",
                        "Have conditions, feed, watering, or treatment changed recently?",
                        "Are other plants or animals affected?"
                ))
                .disclaimer("This is a preliminary decision-support result, not a confirmed diagnosis. A qualified agricultural or veterinary consultant must confirm the condition and treatment.")
                .imageUrl(imageUrl)
                .build();
    }

    private void validateText(String subjectType, String description) {
        if (!StringUtils.hasText(subjectType)) {
            throw new IllegalArgumentException("subjectType is required");
        }
        if (!StringUtils.hasText(description) || description.trim().length() < 10) {
            throw new IllegalArgumentException("description must contain at least 10 characters");
        }
    }

    private String storeImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("A symptom image is required");
        }
        if (image.getSize() > MAX_IMAGE_BYTES) {
            throw new IllegalArgumentException("Image size must not exceed 5 MB");
        }
        if (!ALLOWED_TYPES.contains(image.getContentType())) {
            throw new IllegalArgumentException("Only JPEG, PNG, and WEBP images are allowed");
        }

        String extension = extensionFor(image.getContentType());
        String safeName = UUID.randomUUID() + extension;
        try {
            Files.createDirectories(uploadDirectory);
            Path destination = uploadDirectory.resolve(safeName).normalize();
            if (!destination.startsWith(uploadDirectory)) {
                throw new IllegalArgumentException("Invalid upload path");
            }
            Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + safeName;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to store the uploaded image", ex);
        }
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }

    private boolean containsAny(String value, String... terms) {
        for (String term : terms) {
            if (value.contains(term)) {
                return true;
            }
        }
        return false;
    }
}
