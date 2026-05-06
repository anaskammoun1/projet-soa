package com.soutenance.features.chatbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soutenance.security.Role;
import com.soutenance.security.user.ApplicationUser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocalAiChatServiceTest {

    private final LocalAiChatService service = new LocalAiChatService(new ObjectMapper());

    @Test
    void planningQuestionReturnsUiWorkflowWithoutFakeApiSteps() {
        String answer = service.ask("donne moi un exemple de planification d'une soutenance", admin());

        assertThat(answer)
                .contains("Pour planifier une soutenance")
                .contains("Ajouter une soutenance")
                .contains("jury se choisit directement dans la soutenance")
                .doesNotContain("curl")
                .doesNotContain("CREATE JURY")
                .doesNotContain("api.example.com")
                .doesNotContain("TRACK PROCESS");
    }

    @Test
    void resultQuestionReturnsAppWorkflowWithoutInventedParticipantFlow() {
        String answer = service.ask("comment valider et publier un resultat", admin());

        assertThat(answer)
                .contains("Le parcours des resultats")
                .contains("L admin peut valider puis publier")
                .contains("Il n y a pas de fichier de sortie")
                .doesNotContain("curl")
                .doesNotContain("participants peuvent choisir")
                .doesNotContain("TRACK PROCESS");
    }

    private ApplicationUser admin() {
        return ApplicationUser.builder()
                .username("admin")
                .email("admin@example.local")
                .passwordHash("x")
                .role(Role.ADMIN)
                .build();
    }
}
