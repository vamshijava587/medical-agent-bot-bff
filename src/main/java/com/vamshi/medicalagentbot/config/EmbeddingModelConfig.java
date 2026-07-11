package com.vamshi.medicalagentbot.config;

import com.vamshi.medicalagentbot.common.MedicalAiAgentBotProperties;
import com.vamshi.medicalagentbot.service.JinaEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EmbeddingModelConfig {

    @Bean
    public JinaEmbeddingModel jinaEmbeddingModel(
            WebClient jinaWebClient,
            MedicalAiAgentBotProperties properties) {

        MedicalAiAgentBotProperties.Jina jina = properties.jina();
        return new JinaEmbeddingModel(
                jinaWebClient,
                jina.model(),
                jina.dimensions(),
                jina.task()   // retrieval.query
        );
    }
}