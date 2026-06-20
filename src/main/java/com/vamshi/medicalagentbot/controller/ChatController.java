package com.vamshi.medicalagentbot.controller;

import com.vamshi.medicalagentbot.model.ChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @PostMapping(produces = "text/event-stream")
    public Flux<String> chat(@RequestBody ChatRequest chatRequest){

        return chatClient
                .prompt(chatRequest.message())
                .stream()
                .content();
    }
}
