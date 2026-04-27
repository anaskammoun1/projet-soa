package com.soutenance.security;

import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.service.Interface.SoutenanceService;
import com.soutenance.security.user.ApplicationUser;
import com.soutenance.security.user.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("ownershipSecurity")
@RequiredArgsConstructor
public class OwnershipSecurity {

    private final ApplicationUserRepository userRepository;
    private final SoutenanceService soutenanceService;

    public boolean canAccessEtudiant(Integer etudiantId) {
        ApplicationUser user = currentUser();
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.ENSEIGNANT) {
            return true;
        }
        return user.getRole() == Role.ETUDIANT && etudiantId != null && etudiantId.equals(user.getEtudiantId());
    }

    public boolean canAccessEtudiant(Long etudiantId) {
        return etudiantId != null && canAccessEtudiant(etudiantId.intValue());
    }

    public boolean canEvaluate(Long evaluateurId) {
        ApplicationUser user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        return user.getRole() == Role.ENSEIGNANT
                && evaluateurId != null
                && evaluateurId.equals(user.getEnseignantId());
    }

    public boolean canAccessSoutenance(Long soutenanceId) {
        ApplicationUser user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return true;
        }

        Soutenance soutenance = soutenanceService.getOrThrow(soutenanceId);
        if (user.getRole() == Role.ETUDIANT) {
            return soutenance.getEtudiant() != null
                    && soutenance.getEtudiant().getId().equals(user.getEtudiantId());
        }

        return user.getRole() == Role.ENSEIGNANT
                && user.getEnseignantId() != null
                && ((soutenance.getPresident() != null && user.getEnseignantId().equals(soutenance.getPresident().getId()))
                || (soutenance.getRapporteur() != null && user.getEnseignantId().equals(soutenance.getRapporteur().getId()))
                || (soutenance.getExaminateur() != null && user.getEnseignantId().equals(soutenance.getExaminateur().getId())));
    }

    public boolean canAccessNotes(Long soutenanceId) {
        ApplicationUser user = currentUser();
        return user.getRole() == Role.ADMIN || canAccessSoutenance(soutenanceId);
    }

    private ApplicationUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("No authenticated user");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }
}
