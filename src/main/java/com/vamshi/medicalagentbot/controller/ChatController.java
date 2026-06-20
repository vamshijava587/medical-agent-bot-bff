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
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {

    private final ChatClient chatClient;
    private final String systemPrompt;

    public ChatController(
            ChatClient.Builder chatClientBuilder,
            @Value("classpath:/prompts/systemmessage.st") Resource systemPromptResource) throws IOException {
        this.chatClient = chatClientBuilder.build();
        this.systemPrompt = systemPromptResource.getContentAsString(StandardCharsets.UTF_8);
    }

    @PostMapping(produces = "text/event-stream")
    public Flux<String> chat(@RequestBody ChatRequest chatRequest){

        return chatClient.prompt()
                .system(systemPrompt)
                .user(chatRequest.message())
                .stream()
                .content();
    }
}