package com.vamshi.medicalagentbot.config;

import com.vamshi.medicalagentbot.service.JinaEmbeddingModel;
import io.qdrant.client.QdrantClient;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class VectorStoreConfig {

    @Bean
    @Primary
    public QdrantVectorStore vectorStore(
            QdrantClient qdrantClient,
            JinaEmbeddingModel jinaEmbeddingModel) {

        return QdrantVectorStore.builder(qdrantClient, jinaEmbeddingModel)
                .initializeSchema(true)
                .build();
    }
}