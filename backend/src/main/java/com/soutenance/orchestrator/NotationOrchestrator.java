package com.soutenance.orchestrator;

import com.soutenance.exception.BusinessException;
import com.soutenance.features.enseignant.entity.Enseignant;
import com.soutenance.features.note.dto.NoteDTO;
import com.soutenance.features.resultat.entity.Resultat;
import com.soutenance.features.resultat.service.ResultatService;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.service.Interface.SoutenanceService;
import com.soutenance.security.CurrentUserService;
import com.soutenance.security.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotationOrchestrator {

    private final SoutenanceService soutenanceService;
    private final ResultatService resultatService;
    private final CurrentUserService currentUserService;

    @Transactional
    public NoteDTO saisirNote(NoteDTO dto) {
        Long soutenanceId = dto.getSoutenanceId();
        Long evaluateurId = effectiveEvaluateurId(dto.getEvaluateurId());
        validateEvaluateurRole(soutenanceId, evaluateurId, dto.getRoleJury());
        validateResultCanBeEdited(soutenanceId);
        Soutenance saved = noterSoutenance(
                soutenanceId,
                dto.getRoleJury(),
                dto.getNoteRapport(),
                dto.getNoteExpose(),
                dto.getNoteQuestions());
        return toNoteDTO(saved, dto.getRoleJury(), evaluateurId);
    }

    @Transactional
    public NoteDTO modifierNote(Long id, NoteDTO dto) {
        Long soutenanceId = dto.getSoutenanceId() != null ? dto.getSoutenanceId() : id;
        Long evaluateurId = effectiveEvaluateurId(dto.getEvaluateurId());
        validateEvaluateurRole(soutenanceId, evaluateurId, dto.getRoleJury());
        validateResultCanBeEdited(soutenanceId);
        Soutenance saved = noterSoutenance(
                soutenanceId,
                dto.getRoleJury(),
                dto.getNoteRapport(),
                dto.getNoteExpose(),
                dto.getNoteQuestions());
        return toNoteDTO(saved, dto.getRoleJury(), evaluateurId);
    }

    @Transactional
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
            case "PRESIDENT" -> {
                soutenance.setNotePresident((float) moyenne);
                soutenance.setNotePresidentExpose(noteExpose);
                soutenance.setNotePresidentRapport(noteRapport);
                soutenance.setNotePresidentQuestions(noteQuestions);
            }
            case "RAPPORTEUR" -> {
                soutenance.setNoteRapporteur((float) moyenne);
                soutenance.setNoteRapporteurExpose(noteExpose);
                soutenance.setNoteRapporteurRapport(noteRapport);
                soutenance.setNoteRapporteurQuestions(noteQuestions);
            }
            case "EXAMINATEUR" -> {
                soutenance.setNoteExaminateur((float) moyenne);
                soutenance.setNoteExaminateurExpose(noteExpose);
                soutenance.setNoteExaminateurRapport(noteRapport);
                soutenance.setNoteExaminateurQuestions(noteQuestions);
            }
            default -> throw new BusinessException("Role jury invalide");
        }

        Soutenance saved = soutenanceService.save(soutenance);
        if (saved.getNotePresident() != null && saved.getNoteRapporteur() != null && saved.getNoteExaminateur() != null) {
            calculerResultat(saved);
        }

        return saved;
    }

    @Transactional
    public Resultat calculerResultat(Long soutenanceId) {
        return calculerResultat(soutenanceService.getOrThrow(soutenanceId));
    }

    @Transactional
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
                    valueOrAverage(soutenance.getNotePresidentExpose(), soutenance.getNotePresident()),
                    valueOrAverage(soutenance.getNotePresidentRapport(), soutenance.getNotePresident()),
                    valueOrAverage(soutenance.getNotePresidentQuestions(), soutenance.getNotePresident()),
                    soutenance.getNotePresident().doubleValue(),
                    enseignantLabel(soutenance.getPresident())));
        }

        if (soutenance.getNoteRapporteur() != null) {
            notes.add(new NoteDTO(
                    soutenance.getId() * 10 + 2,
                    soutenance.getId(),
                    soutenance.getRapporteur() != null ? soutenance.getRapporteur().getId() : null,
                    "RAPPORTEUR",
                    valueOrAverage(soutenance.getNoteRapporteurExpose(), soutenance.getNoteRapporteur()),
                    valueOrAverage(soutenance.getNoteRapporteurRapport(), soutenance.getNoteRapporteur()),
                    valueOrAverage(soutenance.getNoteRapporteurQuestions(), soutenance.getNoteRapporteur()),
                    soutenance.getNoteRapporteur().doubleValue(),
                    enseignantLabel(soutenance.getRapporteur())));
        }

        if (soutenance.getNoteExaminateur() != null) {
            notes.add(new NoteDTO(
                    soutenance.getId() * 10 + 3,
                    soutenance.getId(),
                    soutenance.getExaminateur() != null ? soutenance.getExaminateur().getId() : null,
                    "EXAMINATEUR",
                    valueOrAverage(soutenance.getNoteExaminateurExpose(), soutenance.getNoteExaminateur()),
                    valueOrAverage(soutenance.getNoteExaminateurRapport(), soutenance.getNoteExaminateur()),
                    valueOrAverage(soutenance.getNoteExaminateurQuestions(), soutenance.getNoteExaminateur()),
                    soutenance.getNoteExaminateur().doubleValue(),
                    enseignantLabel(soutenance.getExaminateur())));
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

    private void validateResultCanBeEdited(Long soutenanceId) {
        resultatService.getResultatBySoutenanceId(soutenanceId).ifPresent(resultat -> {
            if (Boolean.TRUE.equals(resultat.getValide()) || Boolean.TRUE.equals(resultat.getPublie())) {
                throw new BusinessException("Impossible de modifier les notes apres validation ou publication du resultat");
            }
        });
    }

    private Long effectiveEvaluateurId(Long requestedEvaluateurId) {
        var user = currentUserService.getCurrentUser();
        if (user.getRole() == Role.ADMIN) {
            return requestedEvaluateurId;
        }
        if (user.getRole() == Role.ENSEIGNANT && user.getEnseignantId() != null) {
            return user.getEnseignantId();
        }
        throw new BusinessException("Seuls les membres du jury peuvent saisir une note");
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
        Double moyenne;
        Double expose;
        Double rapport;
        Double questions;
        switch (roleJury.toUpperCase()) {
            case "PRESIDENT" -> {
                moyenne = soutenance.getNotePresident() != null ? soutenance.getNotePresident().doubleValue() : null;
                expose = valueOrAverage(soutenance.getNotePresidentExpose(), soutenance.getNotePresident());
                rapport = valueOrAverage(soutenance.getNotePresidentRapport(), soutenance.getNotePresident());
                questions = valueOrAverage(soutenance.getNotePresidentQuestions(), soutenance.getNotePresident());
            }
            case "RAPPORTEUR" -> {
                moyenne = soutenance.getNoteRapporteur() != null ? soutenance.getNoteRapporteur().doubleValue() : null;
                expose = valueOrAverage(soutenance.getNoteRapporteurExpose(), soutenance.getNoteRapporteur());
                rapport = valueOrAverage(soutenance.getNoteRapporteurRapport(), soutenance.getNoteRapporteur());
                questions = valueOrAverage(soutenance.getNoteRapporteurQuestions(), soutenance.getNoteRapporteur());
            }
            case "EXAMINATEUR" -> {
                moyenne = soutenance.getNoteExaminateur() != null ? soutenance.getNoteExaminateur().doubleValue() : null;
                expose = valueOrAverage(soutenance.getNoteExaminateurExpose(), soutenance.getNoteExaminateur());
                rapport = valueOrAverage(soutenance.getNoteExaminateurRapport(), soutenance.getNoteExaminateur());
                questions = valueOrAverage(soutenance.getNoteExaminateurQuestions(), soutenance.getNoteExaminateur());
            }
            default -> throw new BusinessException("Role jury invalide");
        }

        return new NoteDTO(
                soutenance.getId(),
                soutenance.getId(),
                evaluateurId,
                roleJury.toUpperCase(),
                expose,
                rapport,
                questions,
                moyenne,
                enseignantLabel(enseignantForRole(soutenance, roleJury)));
    }

    private Enseignant enseignantForRole(Soutenance soutenance, String roleJury) {
        return switch (roleJury.toUpperCase()) {
            case "PRESIDENT" -> soutenance.getPresident();
            case "RAPPORTEUR" -> soutenance.getRapporteur();
            case "EXAMINATEUR" -> soutenance.getExaminateur();
            default -> null;
        };
    }

    private String enseignantLabel(Enseignant enseignant) {
        if (enseignant == null) {
            return null;
        }
        return (enseignant.getPrenom() + " " + enseignant.getNom()).trim();
    }

    private Double valueOrAverage(Double note, Float average) {
        if (note != null) {
            return note;
        }
        return average != null ? average.doubleValue() : null;
    }
}
