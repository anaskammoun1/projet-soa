package com.soutenance.features.soutenance.service.Implementation;

import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.entity.StatutSoutenance;
import com.soutenance.features.soutenance.repository.SoutenanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoutenanceServiceImplStatusSyncTest {

    @Mock
    private SoutenanceRepository repository;

    private SoutenanceServiceImpl service;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-05-06T12:00:00Z"), ZoneId.of("UTC"));
        service = new SoutenanceServiceImpl(repository, fixedClock);
    }

    @Test
    void getByIdSetsPlanifieeWhenSoutenanceIsInFuture() {
        Soutenance soutenance = baseSoutenance(10L, LocalDateTime.of(2026, 5, 6, 13, 0), 60, null);
        when(repository.findById(10L)).thenReturn(Optional.of(soutenance));
        when(repository.save(any(Soutenance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = service.getById(10L);

        assertThat(dto.getStatut()).isEqualTo(StatutSoutenance.PLANIFIEE);
        verify(repository).save(soutenance);
    }

    @Test
    void getByIdSetsEnCoursWhenSoutenanceHasStartedAndNotFinished() {
        Soutenance soutenance = baseSoutenance(11L, LocalDateTime.of(2026, 5, 6, 11, 45), 30, StatutSoutenance.PLANIFIEE);
        when(repository.findById(11L)).thenReturn(Optional.of(soutenance));
        when(repository.save(any(Soutenance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = service.getById(11L);

        assertThat(dto.getStatut()).isEqualTo(StatutSoutenance.EN_COURS);
        verify(repository).save(soutenance);
    }

    @Test
    void getByIdSetsTermineeWhenSoutenanceEndTimeHasPassed() {
        Soutenance soutenance = baseSoutenance(12L, LocalDateTime.of(2026, 5, 6, 10, 0), 60, StatutSoutenance.EN_COURS);
        when(repository.findById(12L)).thenReturn(Optional.of(soutenance));
        when(repository.save(any(Soutenance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = service.getById(12L);

        assertThat(dto.getStatut()).isEqualTo(StatutSoutenance.TERMINEE);
        verify(repository).save(soutenance);
    }

    @Test
    void getByIdKeepsAnnuleeWithoutAutomaticTransition() {
        Soutenance soutenance = baseSoutenance(13L, LocalDateTime.of(2026, 5, 6, 9, 0), 60, StatutSoutenance.ANNULEE);
        when(repository.findById(13L)).thenReturn(Optional.of(soutenance));

        var dto = service.getById(13L);

        assertThat(dto.getStatut()).isEqualTo(StatutSoutenance.ANNULEE);
        verify(repository, never()).save(soutenance);
    }

    private Soutenance baseSoutenance(Long id, LocalDateTime date, int duree, StatutSoutenance statut) {
        Soutenance soutenance = new Soutenance();
        soutenance.setId(id);
        soutenance.setTitre("Soutenance test");
        soutenance.setDate(date);
        soutenance.setDuree(duree);
        soutenance.setStatut(statut);
        return soutenance;
    }
}
