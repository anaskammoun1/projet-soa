package com.soutenance.features.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotAskRequest {

    @NotBlank(message = "Le message est obligatoire")
    private String message;
}
