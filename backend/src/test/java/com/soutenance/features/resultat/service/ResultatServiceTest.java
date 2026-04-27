package com.soutenance.features.resultat.service;

import com.soutenance.features.resultat.entity.Resultat;
import com.soutenance.features.resultat.repository.ResultatRepository;
import com.soutenance.security.audit.AuditService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultatServiceTest {

    @Mock
    private ResultatRepository resultatRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ResultatService resultatService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void publishResultatAuditsPublication() {
        Resultat resultat = Resultat.builder()
                .id(3L)
                .soutenanceId(10L)
                .valide(true)
                .publie(false)
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null));

        when(resultatRepository.findById(3L)).thenReturn(Optional.of(resultat));
        when(resultatRepository.save(resultat)).thenReturn(resultat);

        Resultat published = resultatService.publishResultat(3L);

        assertThat(published.getPublie()).isTrue();
        verify(auditService).log("RESULTAT_PUBLISHED", "admin", null, true, "resultatId=3, soutenanceId=10");
    }
}
