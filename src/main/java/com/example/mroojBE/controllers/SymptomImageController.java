package com.example.mroojBE.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@RestController
public class SymptomImageController {

    private static final Pattern SAFE_FILE = Pattern.compile("[a-zA-Z0-9_-]+\\.(jpg|jpeg|png|webp)", Pattern.CASE_INSENSITIVE);
    private final Path uploadDirectory;

    public SymptomImageController(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadDirectory = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> image(@PathVariable String filename) throws IOException {
        if (!SAFE_FILE.matcher(filename).matches()) {
            return ResponseEntity.badRequest().build();
        }

        Path file = uploadDirectory.resolve(filename).normalize();
        if (!file.startsWith(uploadDirectory)) {
            return ResponseEntity.badRequest().build();
        }

        if (!Files.isRegularFile(file)) {
            Optional<Path> legacy = findLegacyCopy(filename);
            if (legacy.isPresent()) {
                Files.createDirectories(uploadDirectory);
                Files.copy(legacy.get(), file);
            }
        }

        if (!Files.isRegularFile(file)) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(file);
        MediaType mediaType = contentType == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(contentType);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePublic())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .body(new FileSystemResource(file));
    }

    private Optional<Path> findLegacyCopy(String filename) {
        Path downloads = Path.of(System.getProperty("user.home"), "Downloads");
        if (!Files.isDirectory(downloads)) {
            return Optional.empty();
        }
        try (Stream<Path> paths = Files.find(downloads, 8,
                (path, attrs) -> attrs.isRegularFile() && path.getFileName().toString().equals(filename))) {
            return paths.findFirst();
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }
}
