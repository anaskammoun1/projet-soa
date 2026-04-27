package com.soutenance.orchestrator;

import com.soutenance.exception.BusinessException;
import com.soutenance.features.enseignant.entity.Enseignant;
import com.soutenance.features.etudiant.entity.Etudiant;
import com.soutenance.features.note.dto.NoteDTO;
import com.soutenance.features.resultat.entity.Resultat;
import com.soutenance.features.resultat.service.ResultatService;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.service.Interface.SoutenanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotationOrchestratorTest {

    @Mock
    private SoutenanceService soutenanceService;

    @Mock
    private ResultatService resultatService;

    @InjectMocks
    private NotationOrchestrator orchestrator;

    @Test
    void saisirNoteStoresAverageForEvaluatorRole() {
        Soutenance soutenance = soutenance();
        when(soutenanceService.getOrThrow(10L)).thenReturn(soutenance);
        when(soutenanceService.save(any(Soutenance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NoteDTO response = orchestrator.saisirNote(new NoteDTO(1L, 10L, 1L, "PRESIDENT", 15.0, 12.0, 18.0, null));

        assertThat(response.getMoyenneEvaluateur()).isEqualTo(15.0);
        assertThat(soutenance.getNotePresident()).isEqualTo(15.0f);
        verify(soutenanceService).save(soutenance);
    }

    @Test
    void saisirNoteTriggersResultCalculationWhenAllJuryNotesExist() {
        Soutenance soutenance = soutenance();
        soutenance.setNotePresident(14.0f);
        soutenance.setNoteRapporteur(15.0f);
        when(soutenanceService.getOrThrow(10L)).thenReturn(soutenance);
        when(soutenanceService.save(any(Soutenance.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(resultatService.calculateResultat(10L, 7L, 14.0f, 15.0f, 16.0f)).thenReturn(Resultat.builder().id(1L).build());

        orchestrator.saisirNote(new NoteDTO(1L, 10L, 3L, "EXAMINATEUR", 16.0, 16.0, 16.0, null));

        verify(resultatService).calculateResultat(10L, 7L, 14.0f, 15.0f, 16.0f);
    }

    @Test
    void saisirNoteRejectsEvaluatorThatDoesNotMatchRole() {
        when(soutenanceService.getOrThrow(10L)).thenReturn(soutenance());

        assertThatThrownBy(() -> orchestrator.saisirNote(new NoteDTO(1L, 10L, 99L, "PRESIDENT", 15.0, 12.0, 18.0, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("n'est pas membre");
    }

    private Soutenance soutenance() {
        Soutenance soutenance = new Soutenance();
        soutenance.setId(10L);
        soutenance.setEtudiant(new Etudiant(7, "Nom", "Prenom", "student@example.local", "M1", "GL", "M2"));
        soutenance.setPresident(new Enseignant(1L, "P", "P", "p@example.local", "Prof", "SI"));
        soutenance.setRapporteur(new Enseignant(2L, "R", "R", "r@example.local", "Prof", "SI"));
        soutenance.setExaminateur(new Enseignant(3L, "E", "E", "e@example.local", "Prof", "SI"));
        return soutenance;
    }
}
