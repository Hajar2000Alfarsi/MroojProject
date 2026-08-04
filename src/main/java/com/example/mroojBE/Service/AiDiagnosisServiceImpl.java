package com.example.mroojBE.Service;

import com.example.mroojBE.DTOs.RequestDTO.AiDiagnosisRequestDTO;
import com.example.mroojBE.DTOs.ResponseDTO.AiDiagnosisResponseDTO;
import com.example.mroojBE.config.AiProperties;
import com.example.mroojBE.exceptions.AiDiagnosisException;
import com.example.mroojBE.repository.AiDiagnosisRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

/**
 * Real Gemini API integration. No new Maven dependency: uses the JDK's
 * built-in java.net.http.HttpClient plus the Jackson ObjectMapper Spring
 * Boot already provides — pom.xml is untouched.
 */
@Service
public class AiDiagnosisServiceImpl implements AiDiagnosisRepository {

    private static final Logger log = LoggerFactory.getLogger(AiDiagnosisServiceImpl.class);

    private final AiProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public AiDiagnosisServiceImpl(AiProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public AiDiagnosisResponseDTO diagnose(AiDiagnosisRequestDTO request, byte[] imageBytes, String imageMimeType) {
        if (!properties.isConfigured()) {
            log.warn("mrooj.ai.gemini.api-key is not set — returning mock diagnosis (dev mode, not a failure).");
            return mockResponse();
        }
        try {
            String requestBody = buildGeminiRequestBody(request, imageBytes, imageMimeType);
            String rawJson = callGemini(requestBody);
            AiDiagnosisResponseDTO dto = objectMapper.readValue(rawJson, AiDiagnosisResponseDTO.class);
            dto.setMock(false);
            return dto;
        } catch (Exception e) {
            // Per NOTE-B1 on Booking.java: never silently swallow / replace
            // with mock data. Surface as a real, catchable error instead.
            throw new AiDiagnosisException("Gemini diagnosis call failed: " + e.getMessage(), e);
        }
    }

    @Override
    public AiDiagnosisResponseDTO diagnoseFromImageUrl(AiDiagnosisRequestDTO request, String imageUrl) {
        byte[] imageBytes = null;
        String mimeType = "image/jpeg";
        if (imageUrl != null && !imageUrl.isBlank()) {
            try {
                HttpRequest req = HttpRequest.newBuilder(URI.create(imageUrl))
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();
                HttpResponse<byte[]> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
                if (resp.statusCode() == 200) {
                    imageBytes = resp.body();
                    String contentType = resp.headers().firstValue("Content-Type").orElse(null);
                    if (contentType != null) mimeType = contentType;
                } else {
                    log.warn("Could not download symptoms image from {} (status={}); continuing text-only.",
                            imageUrl, resp.statusCode());
                }
            } catch (Exception e) {
                log.warn("Error downloading symptoms image from {}; continuing text-only: {}", imageUrl, e.getMessage());
            }
        }
        return diagnose(request, imageBytes, mimeType);
    }

    private String buildGeminiRequestBody(AiDiagnosisRequestDTO request, byte[] imageBytes, String imageMimeType) {
        boolean arabic = request.getPreferredLanguage() == null || "ar".equalsIgnoreCase(request.getPreferredLanguage());

        String systemPrompt = (arabic
                ? "أنت مساعد تشخيص أولي زراعي وبيطري لمنصة مروج في سلطنة عمان. "
                  + "حلل الوصف والصورة المرفقة (إن وجدت) وأعد تشخيصاً أولياً فقط، "
                  + "لأن القرار النهائي يعود دائماً للمستشار البشري المختص. "
                  + "أعد الإجابة بصيغة JSON فقط بدون أي نص إضافي، بالمخطط التالي:"
                : "You are a preliminary agricultural/veterinary triage assistant for the Mrooj "
                  + "platform in Oman. Analyze the description and attached image (if any) and "
                  + "provide a PRELIMINARY diagnosis only, since the final decision always belongs "
                  + "to the human consultant. Return JSON only, no extra text, with this schema:")
                + " {\"probableIssue\":string,\"urgency\":\"LOW|MEDIUM|HIGH\","
                + "\"confidence\":\"LOW|MEDIUM|HIGH\",\"possibleCauses\":[string],"
                + "\"recommendedSteps\":[string],\"disclaimer\":string}";

        String userPrompt = String.format(
                "Domain: %s%nSubject type: %s%nIssue category: %s%nFarmer description: %s",
                request.getDomain(), request.getSubjectType(), request.getIssueCategory(), request.getDescription());

        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode contents = root.putArray("contents");
        ObjectNode content = contents.addObject();
        ArrayNode parts = content.putArray("parts");
        parts.addObject().put("text", systemPrompt + "\n\n" + userPrompt);

        if (imageBytes != null && imageBytes.length > 0) {
            ObjectNode imagePart = parts.addObject();
            ObjectNode inlineData = imagePart.putObject("inline_data");
            inlineData.put("mime_type", imageMimeType != null ? imageMimeType : "image/jpeg");
            inlineData.put("data", Base64.getEncoder().encodeToString(imageBytes));
        }

        root.putObject("generationConfig").put("response_mime_type", "application/json");
        return root.toString();
    }

    private String callGemini(String requestBody) throws Exception {
        String url = properties.getEndpoint() + "/" + properties.getModel()
                + ":generateContent?key=" + properties.getApiKey();

        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Gemini API error, status=" + response.statusCode()
                    + " body=" + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        return root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();
    }

    private AiDiagnosisResponseDTO mockResponse() {
        return AiDiagnosisResponseDTO.builder()
                .probableIssue("وضع تجريبي — لم يتم ضبط mrooj.ai.gemini.api-key بعد")
                .urgency("MEDIUM")
                .confidence("LOW")
                .possibleCauses(List.of("بيانات تجريبية للعرض فقط"))
                .recommendedSteps(List.of("اضبطوا GEMINI_API_KEY في متغيرات البيئة لتفعيل التحليل الحقيقي"))
                .disclaimer("تشخيص أولي تجريبي وليس فعلياً — راجعوا المستشار المختص.")
                .mock(true)
                .build();
    }
}