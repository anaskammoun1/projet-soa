package com.soutenance.orchestrator;

import com.soutenance.exception.BusinessException;
import com.soutenance.features.resultat.entity.Resultat;
import com.soutenance.features.resultat.service.ResultatService;
import com.soutenance.features.resultat.service.ResultatService.ResultatStatistics;
import com.soutenance.security.CurrentUserService;
import com.soutenance.security.audit.AuditService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResultatOrchestrator {

    private final ResultatService resultatService;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    public List<Resultat> getAllResultats() {
        return resultatService.getAllResultats();
    }

    public Optional<Resultat> getResultatById(Long id) {
        return resultatService.getResultatById(id);
    }

    public Optional<Resultat> getResultatBySoutenanceId(Long soutenanceId) {
        return resultatService.getResultatBySoutenanceId(soutenanceId);
    }

    public List<Resultat> getVisiblePublishedResultats() {
        if (currentUserService.isTeacher()) {
            return resultatService.getAssignedToTeacher(currentUserService.getCurrentEnseignantId());
        }
        return resultatService.getPublishedResultats();
    }

    public Optional<Resultat> getPublishedResultatByEtudiant(Long etudiantId) {
        return resultatService.getPublishedResultatByEtudiantId(etudiantId);
    }

    public Optional<Resultat> getMyPublishedResultat() {
        return resultatService.getPublishedResultatByEtudiantId(currentUserService.getCurrentEtudiantId().longValue());
    }

    public Resultat validateResultat(Long id) {
        return resultatService.validateResultat(id);
    }

    @Transactional
    public Resultat publishResultat(Long id) {
        Resultat published = resultatService.publishResultat(id);
        auditService.log(
                "RESULTAT_PUBLISHED",
                currentUserService.getCurrentUser().getUsername(),
                null,
                true,
                "resultatId=" + published.getId() + ", soutenanceId=" + published.getSoutenanceId());
        return published;
    }

    public Resultat publishResultatBySoutenance(Long soutenanceId) {
        return resultatService.getResultatBySoutenanceId(soutenanceId)
                .map(result -> publishResultat(result.getId()))
                .orElseThrow(() -> new BusinessException("Resultat non trouve pour cette soutenance"));
    }

    public ResultatStatistics getStatistics() {
        return resultatService.getStatistics();
    }
}
