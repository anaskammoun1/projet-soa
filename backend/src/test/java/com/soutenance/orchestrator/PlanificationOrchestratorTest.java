package com.soutenance.orchestrator;

import com.soutenance.exception.BusinessException;
import com.soutenance.features.enseignant.entity.Enseignant;
import com.soutenance.features.enseignant.service.Interface.EnseignantService;
import com.soutenance.features.etudiant.entity.Etudiant;
import com.soutenance.features.etudiant.service.Interface.EtudiantService;
import com.soutenance.features.salle.entity.Salle;
import com.soutenance.features.salle.service.Interface.SalleService;
import com.soutenance.features.soutenance.dto.SoutenanceDTO;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.entity.StatutSoutenance;
import com.soutenance.features.soutenance.service.Interface.SoutenanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanificationOrchestratorTest {

    @Mock
    private EtudiantService etudiantService;

    @Mock
    private EnseignantService enseignantService;

    @Mock
    private SalleService salleService;

    @Mock
    private SoutenanceService soutenanceService;

    @InjectMocks
    private PlanificationOrchestrator orchestrator;

    @Test
    void planifierSoutenanceSavesWhenReferencesAndScheduleAreValid() {
        SoutenanceDTO dto = dto();
        Etudiant etudiant = etudiant(7);
        Enseignant president = enseignant(1L);
        Enseignant rapporteur = enseignant(2L);
        Enseignant examinateur = enseignant(3L);
        Salle salle = new Salle(4L, "A1", 20, "Bloc A", true);

        when(etudiantService.getOrThrow(7)).thenReturn(etudiant);
        when(enseignantService.getOrThrow(1L)).thenReturn(president);
        when(enseignantService.getOrThrow(2L)).thenReturn(rapporteur);
        when(enseignantService.getOrThrow(3L)).thenReturn(examinateur);
        when(salleService.getOrThrow(4L)).thenReturn(salle);
        when(soutenanceService.save(any(Soutenance.class))).thenAnswer(invocation -> {
            Soutenance saved = invocation.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        Soutenance saved = orchestrator.planifierSoutenance(dto);

        assertThat(saved.getId()).isEqualTo(99L);
        assertThat(saved.getEtudiant()).isSameAs(etudiant);
        assertThat(saved.getPresident()).isSameAs(president);
        assertThat(saved.getSalle()).isSameAs(salle);
        assertThat(saved.getStatut()).isEqualTo(StatutSoutenance.PLANIFIEE);
        verify(soutenanceService).save(any(Soutenance.class));
    }

    @Test
    void planifierSoutenanceRejectsSalleConflict() {
        SoutenanceDTO dto = dto();
        when(etudiantService.getOrThrow(7)).thenReturn(etudiant(7));
        when(enseignantService.getOrThrow(1L)).thenReturn(enseignant(1L));
        when(enseignantService.getOrThrow(2L)).thenReturn(enseignant(2L));
        when(enseignantService.getOrThrow(3L)).thenReturn(enseignant(3L));
        when(salleService.getOrThrow(4L)).thenReturn(new Salle(4L, "A1", 20, "Bloc A", true));
        when(soutenanceService.existsConflitSalle(4L, dto.getDate(), dto.getDate().plusMinutes(60), null)).thenReturn(true);

        assertThatThrownBy(() -> orchestrator.planifierSoutenance(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Conflit de salle");
        verify(soutenanceService, never()).save(any(Soutenance.class));
    }

    private SoutenanceDTO dto() {
        return new SoutenanceDTO(
                null,
                "Memoire SOA",
                LocalDateTime.of(2026, 5, 4, 10, 0),
                60,
                null,
                1L,
                2L,
                3L,
                4L,
                7,
                null,
                null,
                null);
    }

    private Enseignant enseignant(Long id) {
        return new Enseignant(id, "Nom", "Prenom", "e" + id + "@example.local", "Prof", "SI");
    }

    private Etudiant etudiant(Integer id) {
        return new Etudiant(id, "Nom", "Prenom", "student@example.local", "M1", "GL", "M2");
    }
}
