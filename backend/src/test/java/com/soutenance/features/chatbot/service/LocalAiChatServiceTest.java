package com.soutenance.features.chatbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soutenance.features.chatbot.service.ChatbotKnowledgeService;
import com.soutenance.features.resultat.entity.Resultat;
import com.soutenance.features.resultat.entity.Resultat.Decision;
import com.soutenance.features.resultat.entity.Resultat.Mention;
import com.soutenance.features.resultat.service.ResultatService;
import com.soutenance.security.Role;
import com.soutenance.security.user.ApplicationUser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

class LocalAiChatServiceTest {

    private final LocalAiChatService service = new LocalAiChatService(
            new ObjectMapper(),
            mock(ResultatService.class),
            mock(ChatbotKnowledgeService.class));

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

            @Test
            void averageOfMoyennesQuestionIsAnsweredDirectly() {
            when(resultatService.getAllResultats()).thenReturn(List.of(
                Resultat.builder().moyenneFinale(12.0).mention(Mention.ASSEZ_BIEN).decisionFinale(Decision.ADMIS).build(),
                Resultat.builder().moyenneFinale(16.0).mention(Mention.BIEN).decisionFinale(Decision.ADMIS).build()));

            String answer = service.ask("quel'est l'average des moyennes", admin());

            assertThat(answer)
                .contains("moyenne des moyennes finales disponibles")
                .contains("14,00");
            }

            @Test
            void vagueNotesQuestionAsksForClarification() {
            String answer = service.ask("quels sont ses notes", admin());

            assertThat(answer)
                .contains("De quel etudiant ou de quelle soutenance parles-tu")
                .contains("nom, le titre ou la date");
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
