package com.soutenance.features.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatbotAskResponse {

    private String answer;
    private String model;
}
