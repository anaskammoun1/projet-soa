package com.soutenance.orchestrator;

import com.soutenance.features.soutenance.dto.SoutenanceDTO;
import com.soutenance.features.soutenance.service.Interface.SoutenanceService;
import com.soutenance.security.CurrentUserService;
import com.soutenance.security.Role;
import com.soutenance.security.user.ApplicationUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SoutenanceReadOrchestrator {

    private final SoutenanceService soutenanceService;
    private final CurrentUserService currentUserService;

    public List<SoutenanceDTO> getVisibleSoutenances() {
        ApplicationUser user = currentUserService.getCurrentUser();
        if (user.getRole() == Role.ADMIN) {
            return soutenanceService.getAll();
        }
        if (user.getRole() == Role.ENSEIGNANT) {
            return soutenanceService.getAssignedToTeacher(user.getEnseignantId());
        }
        return soutenanceService.getByEtudiantId(user.getEtudiantId());
    }
}
