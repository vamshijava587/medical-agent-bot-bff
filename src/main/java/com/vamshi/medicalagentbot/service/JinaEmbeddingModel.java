package com.vamshi.medicalagentbot.service;

import com.vamshi.medicalagentbot.model.JinaEmbeddingRequest;
import com.vamshi.medicalagentbot.model.JinaEmbeddingResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class JinaEmbeddingModel implements EmbeddingModel {

    private final WebClient webClient;
    private final String model;
    private final int dimensions;
    private final String task;

    public JinaEmbeddingModel(WebClient webClient,
                              String model,
                              int dimensions,
                              String task) {
        this.webClient = webClient;
        this.model = model;
        this.dimensions = dimensions;
        this.task = task;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        JinaEmbeddingRequest body = new JinaEmbeddingRequest(
                model, task, true, dimensions, request.getInstructions());

        JinaEmbeddingResponse response = webClient.post()
                .uri("/v1/embeddings")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JinaEmbeddingResponse.class)
                .block();

        if (response == null || response.data() == null) {
            throw new IllegalStateException("Empty embedding response from Jina");
        }

        List<Embedding> embeddings = response.data().stream()
                .map(d -> new Embedding(d.embedding(), d.index()))
                .toList();

        return new EmbeddingResponse(embeddings);
    }

    @Override
    public float[] embed(Document document) {
        return embed(document.getText());
    }
}