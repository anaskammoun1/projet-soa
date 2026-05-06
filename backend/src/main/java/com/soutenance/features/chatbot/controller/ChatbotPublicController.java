package com.soutenance.features.chatbot.controller;

import com.soutenance.features.chatbot.service.LocalAiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotPublicController {

    private final LocalAiChatService localAiChatService;

    public ChatbotPublicController(LocalAiChatService localAiChatService) {
        this.localAiChatService = localAiChatService;
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("model", localAiChatService.getModelName()));
    }
}
