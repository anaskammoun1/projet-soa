package com.soutenance.security;

import com.soutenance.features.resultat.entity.Resultat;
import com.soutenance.features.resultat.repository.ResultatRepository;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.repository.SoutenanceRepository;
import com.soutenance.security.user.ApplicationUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("ownershipSecurity")
@RequiredArgsConstructor
public class OwnershipSecurity {

    private final CurrentUserService currentUserService;
    private final SoutenanceRepository soutenanceRepository;
    private final ResultatRepository resultatRepository;

    public boolean canAccessEtudiant(Integer etudiantId) {
        ApplicationUser user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        if (user.getRole() == Role.ENSEIGNANT) {
            return user.getEnseignantId() != null
                    && etudiantId != null
                    && soutenanceRepository.countAssignedToTeacherForEtudiant(user.getEnseignantId(), etudiantId) > 0;
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

    public boolean canEvaluateNote(Long soutenanceId, String roleJury) {
        ApplicationUser user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        if (user.getRole() != Role.ENSEIGNANT || user.getEnseignantId() == null || roleJury == null) {
            return false;
        }

        return soutenanceRepository.findById(soutenanceId)
                .map(soutenance -> switch (roleJury.toUpperCase()) {
                    case "PRESIDENT" -> soutenance.getPresident() != null
                            && user.getEnseignantId().equals(soutenance.getPresident().getId());
                    case "RAPPORTEUR" -> soutenance.getRapporteur() != null
                            && user.getEnseignantId().equals(soutenance.getRapporteur().getId());
                    case "EXAMINATEUR" -> soutenance.getExaminateur() != null
                            && user.getEnseignantId().equals(soutenance.getExaminateur().getId());
                    default -> false;
                })
                .orElse(false);
    }

    public boolean canAccessSoutenance(Long soutenanceId) {
        ApplicationUser user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return true;
        }

        return soutenanceRepository.findById(soutenanceId)
                .map(soutenance -> canAccessSoutenanceEntity(user, soutenance))
                .orElse(false);
    }

    public boolean canAccessNotes(Long soutenanceId) {
        ApplicationUser user = currentUser();
        return user.getRole() == Role.ADMIN || canAccessSoutenance(soutenanceId);
    }

    public boolean canAccessResultat(Long resultatId) {
        ApplicationUser user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        return resultatRepository.findById(resultatId)
                .map(resultat -> canAccessResultatEntity(user, resultat))
                .orElse(false);
    }

    public boolean canAccessResultatBySoutenance(Long soutenanceId) {
        ApplicationUser user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        return resultatRepository.findBySoutenanceId(soutenanceId)
                .map(resultat -> canAccessResultatEntity(user, resultat))
                .orElse(user.getRole() == Role.ENSEIGNANT && canAccessSoutenance(soutenanceId));
    }

    private boolean canAccessResultatEntity(ApplicationUser user, Resultat resultat) {
        if (user.getRole() == Role.ENSEIGNANT) {
            return soutenanceRepository.findById(resultat.getSoutenanceId())
                    .map(soutenance -> canAccessSoutenanceEntity(user, soutenance))
                    .orElse(false);
        }
        return user.getRole() == Role.ETUDIANT
                && Boolean.TRUE.equals(resultat.getPublie())
                && resultat.getEtudiantId() != null
                && user.getEtudiantId() != null
                && resultat.getEtudiantId().equals(user.getEtudiantId().longValue());
    }

    private boolean canAccessSoutenanceEntity(ApplicationUser user, Soutenance soutenance) {
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

    private ApplicationUser currentUser() {
        return currentUserService.getCurrentUser();
    }
}
