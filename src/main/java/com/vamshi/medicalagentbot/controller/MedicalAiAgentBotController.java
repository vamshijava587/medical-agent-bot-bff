package com.vamshi.medicalagentbot.controller;

import com.vamshi.medicalagentbot.model.ChatRequest;
import com.vamshi.medicalagentbot.model.ModelType;
import com.vamshi.medicalagentbot.service.MedicalAiAgentBotService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:4200/")
public class MedicalAiAgentBotController {

    private final MedicalAiAgentBotService chatService;

    public MedicalAiAgentBotController(MedicalAiAgentBotService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(produces = "text/event-stream")
    public Flux<String> chat(@RequestHeader("X-Chat-Model") ModelType model,
                             @RequestBody ChatRequest chatRequest) {
        return chatService.getChatResponse(model, chatRequest.message());
    }
}