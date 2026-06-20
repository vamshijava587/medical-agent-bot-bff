package com.vamshi.medicalagentbot.controller;

import com.vamshi.medicalagentbot.model.ChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient chatClient;
    private final String systemPrompt;
    private final String[] allowedOrigins;

    public ChatController(
            ChatClient.Builder chatClientBuilder,
            @Value("classpath:/prompts/systemmessage.st") Resource systemPromptResource,
            @Value("${app.crossOrigin.allowed-origins}") String[] allowedOrigins) throws IOException {
        this.chatClient = chatClientBuilder.build();
        this.systemPrompt = systemPromptResource.getContentAsString(StandardCharsets.UTF_8);
        this.allowedOrigins = allowedOrigins;
    }

    @CrossOrigin(origins = "${app.crossOrigin.allowed-origins}")
    @PostMapping(produces = "text/event-stream")
    public Flux<String> chat(@RequestBody ChatRequest chatRequest){
        return chatClient.prompt()
                .system(systemPrompt)
                .user(chatRequest.message())
                .stream()
                .content();
    }
}