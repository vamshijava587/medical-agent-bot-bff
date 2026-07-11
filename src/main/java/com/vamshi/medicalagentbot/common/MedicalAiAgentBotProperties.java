package com.vamshi.medicalagentbot.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "medicalai")
public record MedicalAiAgentBotProperties(Jina jina) {
    public record Jina(
            String apiKey,
            String baseUrl,
            String model,
            int dimensions,
            String task
    ) {}
}