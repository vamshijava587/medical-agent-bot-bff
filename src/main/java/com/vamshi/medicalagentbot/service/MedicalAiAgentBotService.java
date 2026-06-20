package com.vamshi.medicalagentbot.service;

import com.vamshi.medicalagentbot.model.ModelType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class MedicalAiAgentBotService {

    private final Map<ModelType, ChatClient> chatClients;
    private final String systemPrompt;

    public MedicalAiAgentBotService(
            @Qualifier("openAiChatClient") ChatClient openAiChatClient,
            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient,
            @Value("classpath:/prompts/systemmessage.st") Resource systemPromptResource) throws IOException {
        this.chatClients = Map.of(
                ModelType.OPENAI, openAiChatClient,
                ModelType.OLLAMA, ollamaChatClient
        );
        this.systemPrompt = systemPromptResource.getContentAsString(StandardCharsets.UTF_8);
    }

    public Flux<String> getChatResponse(ModelType modelType, String userMessage) {
        ChatClient chatClient = chatClients.get(modelType);
        if (chatClient == null) {
            return Flux.error(new IllegalArgumentException("Unsupported model type: " + modelType));
        }

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .stream()
                .content();
    }
}