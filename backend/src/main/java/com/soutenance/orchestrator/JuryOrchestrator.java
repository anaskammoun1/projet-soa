package com.soutenance.orchestrator;

import com.soutenance.exception.BusinessException;
import com.soutenance.features.enseignant.entity.Enseignant;
import com.soutenance.features.enseignant.service.Interface.EnseignantService;
import com.soutenance.features.jury.dto.JuryDTO;
import com.soutenance.features.soutenance.dto.SoutenanceDTO;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.service.Interface.SoutenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JuryOrchestrator {

    private final EnseignantService enseignantService;
    private final SoutenanceService soutenanceService;

    public SoutenanceDTO affecterJury(JuryDTO dto) {
        Soutenance soutenance = createJury(
                dto.getSoutenanceId(),
                dto.getPresidentId(),
                dto.getRapporteurId(),
                dto.getExaminateurId());
        return toSoutenanceDTO(soutenance);
    }

    public Soutenance createJury(Long soutenanceId,
                                 Long presidentId,
                                 Long rapporteurId,
                                 Long examinateurId) {

        if (presidentId.equals(rapporteurId)
                || presidentId.equals(examinateurId)
                || rapporteurId.equals(examinateurId)) {
            throw new BusinessException("Les membres du jury doivent être différents");
        }

        Enseignant president = enseignantService.getOrThrow(presidentId);
        Enseignant rapporteur = enseignantService.getOrThrow(rapporteurId);
        Enseignant examinateur = enseignantService.getOrThrow(examinateurId);
        Soutenance soutenance = soutenanceService.getOrThrow(soutenanceId);

        soutenance.setPresident(president);
        soutenance.setRapporteur(rapporteur);
        soutenance.setExaminateur(examinateur);

        return soutenanceService.save(soutenance);
    }

    public JuryDTO getBySoutenance(Long soutenanceId) {
        Soutenance soutenance = soutenanceService.getOrThrow(soutenanceId);
        return new JuryDTO(
                soutenance.getId(),
                soutenance.getPresident() != null ? soutenance.getPresident().getId() : null,
                soutenance.getRapporteur() != null ? soutenance.getRapporteur().getId() : null,
                soutenance.getExaminateur() != null ? soutenance.getExaminateur().getId() : null);
    }

    public void deleteJury(Long soutenanceId) {
        Soutenance soutenance = soutenanceService.getOrThrow(soutenanceId);
        soutenance.setPresident(null);
        soutenance.setRapporteur(null);
        soutenance.setExaminateur(null);
        soutenance.setNotePresident(null);
        soutenance.setNoteRapporteur(null);
        soutenance.setNoteExaminateur(null);
        soutenanceService.save(soutenance);
    }

    private SoutenanceDTO toSoutenanceDTO(Soutenance s) {
        return new SoutenanceDTO(
                s.getId(),
                s.getTitre(),
                s.getDate(),
                s.getDuree(),
                s.getStatut(),
                s.getPresident() != null ? s.getPresident().getId() : null,
                s.getRapporteur() != null ? s.getRapporteur().getId() : null,
                s.getExaminateur() != null ? s.getExaminateur().getId() : null,
                s.getSalle() != null ? s.getSalle().getId() : null,
                s.getEtudiant() != null ? s.getEtudiant().getId() : null,
                s.getNotePresident(),
                s.getNoteRapporteur(),
                s.getNoteExaminateur());
    }
}
