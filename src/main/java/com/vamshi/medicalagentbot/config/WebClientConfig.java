package com.vamshi.medicalagentbot.config;

import com.vamshi.medicalagentbot.common.MedicalAiAgentBotProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final int MAX_IN_MEMORY_SIZE = 16 * 1024 * 1024;

    @Bean
    public WebClient jinaWebClient(MedicalAiAgentBotProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.jina().baseUrl())
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(c -> c.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE))
                        .build())
                .defaultHeader("Authorization", "Bearer " + properties.jina().apiKey())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}