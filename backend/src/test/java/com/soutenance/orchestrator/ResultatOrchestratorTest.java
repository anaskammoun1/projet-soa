package com.soutenance.orchestrator;

import com.soutenance.features.resultat.entity.Resultat;
import com.soutenance.features.resultat.service.ResultatService;
import com.soutenance.security.CurrentUserService;
import com.soutenance.security.Role;
import com.soutenance.security.audit.AuditService;
import com.soutenance.security.user.ApplicationUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultatOrchestratorTest {

    @Mock
    private ResultatService resultatService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ResultatOrchestrator resultatOrchestrator;

    @Test
    void publishResultatAuditsPublicationAtWorkflowLayer() {
        Resultat published = Resultat.builder()
                .id(3L)
                .soutenanceId(10L)
                .publie(true)
                .build();
        when(resultatService.publishResultat(3L)).thenReturn(published);
        when(currentUserService.getCurrentUser()).thenReturn(ApplicationUser.builder()
                .username("admin")
                .role(Role.ADMIN)
                .build());

        Resultat result = resultatOrchestrator.publishResultat(3L);

        assertThat(result).isSameAs(published);
        verify(auditService).log("RESULTAT_PUBLISHED", "admin", null, true, "resultatId=3, soutenanceId=10");
    }
}
