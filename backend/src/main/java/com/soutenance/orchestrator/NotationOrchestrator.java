package com.soutenance.orchestrator;

import com.soutenance.exception.BusinessException;
import com.soutenance.features.note.dto.NoteDTO;
import com.soutenance.features.resultat.entity.Resultat;
import com.soutenance.features.resultat.service.ResultatService;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.service.Interface.SoutenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotationOrchestrator {

    private final SoutenanceService soutenanceService;
    private final ResultatService resultatService;

    public NoteDTO saisirNote(NoteDTO dto) {
        Long soutenanceId = dto.getSoutenanceId();
        validateEvaluateurRole(soutenanceId, dto.getEvaluateurId(), dto.getRoleJury());
        Soutenance saved = noterSoutenance(
                soutenanceId,
                dto.getRoleJury(),
                dto.getNoteRapport(),
                dto.getNoteExpose(),
                dto.getNoteQuestions());
        return toNoteDTO(saved, dto.getRoleJury(), dto.getEvaluateurId());
    }

    public NoteDTO modifierNote(Long id, NoteDTO dto) {
        Long soutenanceId = dto.getSoutenanceId() != null ? dto.getSoutenanceId() : id;
        validateEvaluateurRole(soutenanceId, dto.getEvaluateurId(), dto.getRoleJury());
        Soutenance saved = noterSoutenance(
                soutenanceId,
                dto.getRoleJury(),
                dto.getNoteRapport(),
                dto.getNoteExpose(),
                dto.getNoteQuestions());
        return toNoteDTO(saved, dto.getRoleJury(), dto.getEvaluateurId());
    }

    public Soutenance noterSoutenance(Long soutenanceId,
                                      String roleJury,
                                      double noteRapport,
                                      double noteExpose,
                                      double noteQuestions) {

        if (noteRapport < 0 || noteRapport > 20
                || noteExpose < 0 || noteExpose > 20
                || noteQuestions < 0 || noteQuestions > 20) {
            throw new BusinessException("Note invalide");
        }

        Soutenance soutenance = soutenanceService.getOrThrow(soutenanceId);

        if (roleJury == null || roleJury.isBlank()) {
            throw new BusinessException("Le role du jury est obligatoire");
        }

        double moyenne = (noteRapport + noteExpose + noteQuestions) / 3.0;

        switch (roleJury.toUpperCase()) {
            case "PRESIDENT" -> soutenance.setNotePresident((float) moyenne);
            case "RAPPORTEUR" -> soutenance.setNoteRapporteur((float) moyenne);
            case "EXAMINATEUR" -> soutenance.setNoteExaminateur((float) moyenne);
            default -> throw new BusinessException("Role jury invalide");
        }

        Soutenance saved = soutenanceService.save(soutenance);
        if (saved.getNotePresident() != null && saved.getNoteRapporteur() != null && saved.getNoteExaminateur() != null) {
            calculerResultat(saved);
        }

        return saved;
    }

    public Resultat calculerResultat(Long soutenanceId) {
        return calculerResultat(soutenanceService.getOrThrow(soutenanceId));
    }

    public Resultat calculerResultat(Long soutenanceId, Long etudiantId) {
        Soutenance soutenance = soutenanceService.getOrThrow(soutenanceId);
        if (soutenance.getEtudiant() == null || !soutenance.getEtudiant().getId().equals(etudiantId.intValue())) {
            throw new BusinessException("L'etudiant ne correspond pas a la soutenance");
        }
        return calculerResultat(soutenance);
    }

    public List<NoteDTO> getNotesBySoutenance(Long soutenanceId) {
        Soutenance soutenance = soutenanceService.getOrThrow(soutenanceId);
        List<NoteDTO> notes = new ArrayList<>();

        if (soutenance.getNotePresident() != null) {
            notes.add(new NoteDTO(
                    soutenance.getId() * 10 + 1,
                    soutenance.getId(),
                    soutenance.getPresident() != null ? soutenance.getPresident().getId() : null,
                    "PRESIDENT",
                    soutenance.getNotePresident().doubleValue(),
                    soutenance.getNotePresident().doubleValue(),
                    soutenance.getNotePresident().doubleValue(),
                    soutenance.getNotePresident().doubleValue()));
        }

        if (soutenance.getNoteRapporteur() != null) {
            notes.add(new NoteDTO(
                    soutenance.getId() * 10 + 2,
                    soutenance.getId(),
                    soutenance.getRapporteur() != null ? soutenance.getRapporteur().getId() : null,
                    "RAPPORTEUR",
                    soutenance.getNoteRapporteur().doubleValue(),
                    soutenance.getNoteRapporteur().doubleValue(),
                    soutenance.getNoteRapporteur().doubleValue(),
                    soutenance.getNoteRapporteur().doubleValue()));
        }

        if (soutenance.getNoteExaminateur() != null) {
            notes.add(new NoteDTO(
                    soutenance.getId() * 10 + 3,
                    soutenance.getId(),
                    soutenance.getExaminateur() != null ? soutenance.getExaminateur().getId() : null,
                    "EXAMINATEUR",
                    soutenance.getNoteExaminateur().doubleValue(),
                    soutenance.getNoteExaminateur().doubleValue(),
                    soutenance.getNoteExaminateur().doubleValue(),
                    soutenance.getNoteExaminateur().doubleValue()));
        }

        return notes;
    }

    private Resultat calculerResultat(Soutenance soutenance) {
        Long etudiantId = soutenance.getEtudiant() != null ? soutenance.getEtudiant().getId().longValue() : null;
        return resultatService.calculateResultat(
                soutenance.getId(),
                etudiantId,
                soutenance.getNotePresident(),
                soutenance.getNoteRapporteur(),
                soutenance.getNoteExaminateur());
    }

    private void validateEvaluateurRole(Long soutenanceId, Long evaluateurId, String roleJury) {
        Soutenance soutenance = soutenanceService.getOrThrow(soutenanceId);
        if (evaluateurId == null || roleJury == null) {
            throw new BusinessException("L'evaluateur et le role sont obligatoires");
        }

        boolean valid;
        switch (roleJury.toUpperCase()) {
            case "PRESIDENT" -> valid = soutenance.getPresident() != null
                    && soutenance.getPresident().getId().equals(evaluateurId);
            case "RAPPORTEUR" -> valid = soutenance.getRapporteur() != null
                    && soutenance.getRapporteur().getId().equals(evaluateurId);
            case "EXAMINATEUR" -> valid = soutenance.getExaminateur() != null
                    && soutenance.getExaminateur().getId().equals(evaluateurId);
            default -> throw new BusinessException("Role jury invalide");
        }

        if (!valid) {
            throw new BusinessException("L'evaluateur n'est pas membre du jury pour ce role");
        }
    }

    private NoteDTO toNoteDTO(Soutenance soutenance, String roleJury, Long evaluateurId) {
        Double value;
        switch (roleJury.toUpperCase()) {
            case "PRESIDENT" -> value = soutenance.getNotePresident() != null
                    ? soutenance.getNotePresident().doubleValue() : null;
            case "RAPPORTEUR" -> value = soutenance.getNoteRapporteur() != null
                    ? soutenance.getNoteRapporteur().doubleValue() : null;
            case "EXAMINATEUR" -> value = soutenance.getNoteExaminateur() != null
                    ? soutenance.getNoteExaminateur().doubleValue() : null;
            default -> throw new BusinessException("Role jury invalide");
        }

        return new NoteDTO(
                soutenance.getId(),
                soutenance.getId(),
                evaluateurId,
                roleJury.toUpperCase(),
                value,
                value,
                value,
                value);
    }
}
