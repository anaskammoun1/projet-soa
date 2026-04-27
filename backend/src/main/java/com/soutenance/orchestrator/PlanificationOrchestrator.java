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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PlanificationOrchestrator {

    private final EtudiantService etudiantService;
    private final EnseignantService enseignantService;
    private final SalleService salleService;
    private final SoutenanceService soutenanceService;

    public Soutenance planifierSoutenance(SoutenanceDTO dto) {
        validateRequired(dto);
        Soutenance soutenance = new Soutenance();
        applyDto(soutenance, dto);
        return planifierSoutenance(soutenance, null);
    }

    public Soutenance planifierSoutenance(Soutenance soutenance) {
        return planifierSoutenance(soutenance, null);
    }

    public Soutenance modifierSoutenance(Long id, SoutenanceDTO dto) {
        Soutenance existing = soutenanceService.getOrThrow(id);
        Soutenance merged = merge(existing, dto);
        validateRequired(toDtoShape(merged));
        return planifierSoutenance(merged, id);
    }

    private Soutenance planifierSoutenance(Soutenance soutenance, Long excludeSoutenanceId) {
        Etudiant etudiant = etudiantService.getOrThrow(soutenance.getEtudiant().getId());
        Enseignant president = enseignantService.getOrThrow(soutenance.getPresident().getId());
        Enseignant rapporteur = enseignantService.getOrThrow(soutenance.getRapporteur().getId());
        Enseignant examinateur = enseignantService.getOrThrow(soutenance.getExaminateur().getId());
        Salle salle = salleService.getOrThrow(soutenance.getSalle().getId());

        LocalDateTime debut = soutenance.getDate();
        LocalDateTime fin = debut.plusMinutes(soutenance.getDuree());

        if (!salle.isDisponible()) {
            throw new BusinessException("Salle non disponible");
        }

        if (soutenanceService.existsConflitSalle(salle.getId(), debut, fin, excludeSoutenanceId)) {
            throw new BusinessException("Conflit de salle");
        }

        if (soutenanceService.existsConflitEncadrant(president.getId(), debut, fin, excludeSoutenanceId)
                || soutenanceService.existsConflitEncadrant(rapporteur.getId(), debut, fin, excludeSoutenanceId)
                || soutenanceService.existsConflitEncadrant(examinateur.getId(), debut, fin, excludeSoutenanceId)) {
            throw new BusinessException("Conflit horaire pour un enseignant");
        }

        if (soutenanceService.existsConflitEtudiant(etudiant.getId(), debut, fin, excludeSoutenanceId)) {
            throw new BusinessException("Conflit horaire pour l'etudiant");
        }

        soutenance.setEtudiant(etudiant);
        soutenance.setPresident(president);
        soutenance.setRapporteur(rapporteur);
        soutenance.setExaminateur(examinateur);
        soutenance.setSalle(salle);
        if (soutenance.getStatut() == null) {
            soutenance.setStatut(StatutSoutenance.PLANIFIEE);
        }

        return soutenanceService.save(soutenance);
    }

    private void validateRequired(SoutenanceDTO dto) {
        if (dto.getTitre() == null || dto.getTitre().isBlank()
                || dto.getDate() == null
                || dto.getDuree() <= 0
                || dto.getPresidentId() == null
                || dto.getRapporteurId() == null
                || dto.getExaminateurId() == null
                || dto.getSalleId() == null
                || dto.getEtudiantId() == null) {
            throw new BusinessException("Champs obligatoires manquants pour planifier une soutenance");
        }

        if (dto.getPresidentId().equals(dto.getRapporteurId())
                || dto.getPresidentId().equals(dto.getExaminateurId())
                || dto.getRapporteurId().equals(dto.getExaminateurId())) {
            throw new BusinessException("Les membres du jury doivent etre differents");
        }
    }

    private Soutenance merge(Soutenance existing, SoutenanceDTO dto) {
        Soutenance merged = new Soutenance();
        merged.setId(existing.getId());
        merged.setTitre(dto.getTitre() != null ? dto.getTitre() : existing.getTitre());
        merged.setDate(dto.getDate() != null ? dto.getDate() : existing.getDate());
        merged.setDuree(dto.getDuree() > 0 ? dto.getDuree() : existing.getDuree());
        merged.setStatut(dto.getStatut() != null ? dto.getStatut() : existing.getStatut());
        merged.setNotePresident(dto.getNotePresident() != null ? dto.getNotePresident() : existing.getNotePresident());
        merged.setNoteRapporteur(dto.getNoteRapporteur() != null ? dto.getNoteRapporteur() : existing.getNoteRapporteur());
        merged.setNoteExaminateur(dto.getNoteExaminateur() != null ? dto.getNoteExaminateur() : existing.getNoteExaminateur());

        Long presidentId = dto.getPresidentId() != null ? dto.getPresidentId() : idOf(existing.getPresident());
        Long rapporteurId = dto.getRapporteurId() != null ? dto.getRapporteurId() : idOf(existing.getRapporteur());
        Long examinateurId = dto.getExaminateurId() != null ? dto.getExaminateurId() : idOf(existing.getExaminateur());
        Long salleId = dto.getSalleId() != null ? dto.getSalleId() : existing.getSalle() != null ? existing.getSalle().getId() : null;
        Integer etudiantId = dto.getEtudiantId() != null ? dto.getEtudiantId() : existing.getEtudiant() != null ? existing.getEtudiant().getId() : null;

        merged.setPresident(referenceEnseignant(presidentId));
        merged.setRapporteur(referenceEnseignant(rapporteurId));
        merged.setExaminateur(referenceEnseignant(examinateurId));
        merged.setSalle(referenceSalle(salleId));
        merged.setEtudiant(referenceEtudiant(etudiantId));
        return merged;
    }

    private void applyDto(Soutenance soutenance, SoutenanceDTO dto) {
        soutenance.setId(dto.getId());
        soutenance.setTitre(dto.getTitre());
        soutenance.setDate(dto.getDate());
        soutenance.setDuree(dto.getDuree());
        soutenance.setStatut(dto.getStatut());
        soutenance.setNotePresident(dto.getNotePresident());
        soutenance.setNoteRapporteur(dto.getNoteRapporteur());
        soutenance.setNoteExaminateur(dto.getNoteExaminateur());
        soutenance.setPresident(referenceEnseignant(dto.getPresidentId()));
        soutenance.setRapporteur(referenceEnseignant(dto.getRapporteurId()));
        soutenance.setExaminateur(referenceEnseignant(dto.getExaminateurId()));
        soutenance.setSalle(referenceSalle(dto.getSalleId()));
        soutenance.setEtudiant(referenceEtudiant(dto.getEtudiantId()));
    }

    private SoutenanceDTO toDtoShape(Soutenance soutenance) {
        return new SoutenanceDTO(
                soutenance.getId(),
                soutenance.getTitre(),
                soutenance.getDate(),
                soutenance.getDuree(),
                soutenance.getStatut(),
                idOf(soutenance.getPresident()),
                idOf(soutenance.getRapporteur()),
                idOf(soutenance.getExaminateur()),
                soutenance.getSalle() != null ? soutenance.getSalle().getId() : null,
                soutenance.getEtudiant() != null ? soutenance.getEtudiant().getId() : null,
                soutenance.getNotePresident(),
                soutenance.getNoteRapporteur(),
                soutenance.getNoteExaminateur());
    }

    private Long idOf(Enseignant enseignant) {
        return enseignant != null ? enseignant.getId() : null;
    }

    private Enseignant referenceEnseignant(Long id) {
        if (id == null) {
            return null;
        }
        Enseignant enseignant = new Enseignant();
        enseignant.setId(id);
        return enseignant;
    }

    private Salle referenceSalle(Long id) {
        if (id == null) {
            return null;
        }
        Salle salle = new Salle();
        salle.setId(id);
        return salle;
    }

    private Etudiant referenceEtudiant(Integer id) {
        if (id == null) {
            return null;
        }
        return new Etudiant(id, "", "", "", "", "", "");
    }
}
