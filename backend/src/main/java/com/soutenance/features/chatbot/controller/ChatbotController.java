package com.soutenance.features.chatbot.controller;

import com.soutenance.features.chatbot.dto.ChatbotAskRequest;
import com.soutenance.features.chatbot.dto.ChatbotAskResponse;
import com.soutenance.features.chatbot.service.LocalAiChatService;
import com.soutenance.security.CurrentUserService;
import com.soutenance.security.user.ApplicationUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final LocalAiChatService localAiChatService;
    private final CurrentUserService currentUserService;

    @PostMapping("/ask")
    public ChatbotAskResponse ask(@Valid @RequestBody ChatbotAskRequest request) {
        ApplicationUser user = currentUserService.getCurrentUser();
        try {
            String answer = localAiChatService.ask(request.getMessage(), user);
            return new ChatbotAskResponse(answer, localAiChatService.getModelName());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), ex);
        }
    }
}
