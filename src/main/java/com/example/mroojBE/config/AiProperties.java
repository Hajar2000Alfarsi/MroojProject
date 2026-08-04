package com.example.mroojBE.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bound from application.properties under the mrooj.ai.gemini.* prefix.
 * See application-ai.properties.snippet for the exact lines to add.
 */
@Component
@ConfigurationProperties(prefix = "mrooj.ai.gemini")
public class AiProperties {

    private String apiKey = "";
    private String model = "gemini-2.0-flash";
    private String endpoint = "https://generativelanguage.googleapis.com/v1beta/models";

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}