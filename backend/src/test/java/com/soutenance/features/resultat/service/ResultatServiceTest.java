package com.soutenance.features.resultat.service;

import com.soutenance.features.resultat.entity.Resultat;
import com.soutenance.features.resultat.repository.ResultatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultatServiceTest {

    @Mock
    private ResultatRepository resultatRepository;

    @InjectMocks
    private ResultatService resultatService;

    @Test
    void publishResultatOnlyUpdatesResultState() {
        Resultat resultat = Resultat.builder()
                .id(3L)
                .soutenanceId(10L)
                .valide(true)
                .publie(false)
                .build();

        when(resultatRepository.findById(3L)).thenReturn(Optional.of(resultat));
        when(resultatRepository.save(resultat)).thenReturn(resultat);

        Resultat published = resultatService.publishResultat(3L);

        assertThat(published.getPublie()).isTrue();
        assertThat(published.getPublishedAt()).isNotNull();
    }
}
