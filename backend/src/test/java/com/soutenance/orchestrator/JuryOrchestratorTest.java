package com.soutenance.orchestrator;

import com.soutenance.exception.BusinessException;
import com.soutenance.features.enseignant.entity.Enseignant;
import com.soutenance.features.enseignant.service.Interface.EnseignantService;
import com.soutenance.features.jury.dto.JuryDTO;
import com.soutenance.features.soutenance.dto.SoutenanceDTO;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JuryOrchestratorTest {

    @Mock
    private EnseignantService enseignantService;

    @Mock
    private SoutenanceService soutenanceService;

    @InjectMocks
    private JuryOrchestrator orchestrator;

    @Test
    void affecterJuryAssignsDistinctTeachers() {
        Soutenance soutenance = new Soutenance();
        soutenance.setId(10L);
        when(enseignantService.getOrThrow(1L)).thenReturn(enseignant(1L));
        when(enseignantService.getOrThrow(2L)).thenReturn(enseignant(2L));
        when(enseignantService.getOrThrow(3L)).thenReturn(enseignant(3L));
        when(soutenanceService.getOrThrow(10L)).thenReturn(soutenance);
        when(soutenanceService.save(any(Soutenance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SoutenanceDTO dto = orchestrator.affecterJury(new JuryDTO(10L, 1L, 2L, 3L));

        assertThat(dto.getPresidentId()).isEqualTo(1L);
        assertThat(dto.getRapporteurId()).isEqualTo(2L);
        assertThat(dto.getExaminateurId()).isEqualTo(3L);
        verify(soutenanceService).save(soutenance);
    }

    @Test
    void affecterJuryRejectsDuplicateTeachers() {
        assertThatThrownBy(() -> orchestrator.affecterJury(new JuryDTO(10L, 1L, 1L, 3L)))
                .isInstanceOf(BusinessException.class);
        verify(soutenanceService, never()).save(any(Soutenance.class));
    }

    private Enseignant enseignant(Long id) {
        return new Enseignant(id, "Nom", "Prenom", "e" + id + "@example.local", "Prof", "SI");
    }
}
