package com.vamshi.medicalagentbot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MedicalAiAgentBotConfig {

    @Bean
    @Primary
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel, QuestionAnswerAdvisor ragAdvisor) {
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(ragAdvisor,new TokenUsageLoggerAdvisor())
                .build();
    }

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel, QuestionAnswerAdvisor ragAdvisor) {
        return ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(ragAdvisor,new TokenUsageLoggerAdvisor())
                .build();
    }
        @Bean
        public QuestionAnswerAdvisor questionAnswerAdvisor(
                VectorStore vectorStore) {

            return QuestionAnswerAdvisor.builder(vectorStore)
                    .searchRequest(
                            SearchRequest.builder()
                                    .topK(5)
                                    .similarityThreshold(0.7)
                                    .build()
                    )
                    .build();

        }

}