package com.soutenance.features.note.service;

import com.soutenance.features.note.dto.NoteDTO;
import com.soutenance.features.encadrant.entity.Encadrant;
import com.soutenance.features.encadrant.repository.EncadrantRepository;
import com.soutenance.features.jury.entity.Jury;
import com.soutenance.features.note.entity.Note;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.exception.BusinessException;
import com.soutenance.exception.ResourceNotFoundException;
import com.soutenance.features.jury.repository.JuryRepository;
import com.soutenance.features.note.repository.NoteRepository;
import com.soutenance.features.soutenance.repository.SoutenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final SoutenanceRepository soutenanceRepository;
    private final JuryRepository juryRepository;
    private final EncadrantRepository encadrantRepository;

    public List<NoteDTO> findBySoutenance(Long soutenanceId) {
        return noteRepository.findBySoutenanceId(soutenanceId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public boolean existsBySoutenanceAndEvaluateur(Long soutenanceId, Long evaluateurId) {
        return noteRepository.existsBySoutenanceIdAndEvaluateurId(soutenanceId, evaluateurId);
    }

    @Transactional
    public NoteDTO saisirNote(NoteDTO dto) {
        Encadrant evaluateur = getEncadrantOrThrow(dto.getEvaluateurId());
        Soutenance soutenance = soutenanceRepository.findById(dto.getSoutenanceId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Soutenance introuvable : " + dto.getSoutenanceId()));

        Note note = new Note();
        note.setSoutenance(soutenance);
        note.setEvaluateur(evaluateur);
        note.setNoteExpose(dto.getNoteExpose());
        note.setNoteRapport(dto.getNoteRapport());
        note.setNoteQuestions(dto.getNoteQuestions());
        note.setCommentaire(dto.getCommentaire());

        return toDTO(save(note));
    }

        @Transactional
        public Note save(Note note) {
        if (note.getSoutenance() == null || note.getSoutenance().getId() == null) {
            throw new BusinessException("La soutenance est obligatoire.");
        }
        if (note.getEvaluateur() == null || note.getEvaluateur().getId() == null) {
            throw new BusinessException("L'évaluateur est obligatoire.");
        }

        Long soutenanceId = note.getSoutenance().getId();
        Long evaluateurId = note.getEvaluateur().getId();

        Soutenance soutenance = soutenanceRepository.findById(soutenanceId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Soutenance introuvable : " + soutenanceId));

        if (soutenance.getStatut() == Soutenance.StatutSoutenance.ANNULEE) {
            throw new BusinessException("Impossible de saisir des notes pour une soutenance annulée.");
        }

        Jury jury = juryRepository.findBySoutenanceId(soutenanceId)
            .orElseThrow(() -> new BusinessException(
                "Aucun jury n'a été affecté à cette soutenance. Affecter un jury d'abord."));

        Encadrant evaluateur = getEncadrantOrThrow(evaluateurId);

        boolean estMembre = evaluateur.getId().equals(jury.getPresident().getId())
            || evaluateur.getId().equals(jury.getRapporteur().getId())
            || evaluateur.getId().equals(jury.getExaminateur().getId());

        if (!estMembre) {
            throw new BusinessException(
                evaluateur.getPrenom() + " " + evaluateur.getNom()
                    + " n'est pas membre du jury de cette soutenance.");
        }

        Note toSave = noteRepository
            .findBySoutenanceIdAndEvaluateurId(soutenanceId, evaluateurId)
            .orElse(note);

        toSave.setSoutenance(soutenance);
        toSave.setEvaluateur(evaluateur);
        toSave.setRoleJury(determinerRole(evaluateur, jury));
        toSave.setNoteExpose(note.getNoteExpose());
        toSave.setNoteRapport(note.getNoteRapport());
        toSave.setNoteQuestions(note.getNoteQuestions());
        toSave.setCommentaire(note.getCommentaire());

        return noteRepository.save(toSave);
        }

    @Transactional
    public NoteDTO modifierNote(Long noteId, NoteDTO dto) {
        Note existing = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note introuvable : " + noteId));

        existing.setNoteExpose(dto.getNoteExpose());
        existing.setNoteRapport(dto.getNoteRapport());
        existing.setNoteQuestions(dto.getNoteQuestions());
        existing.setCommentaire(dto.getCommentaire());

        return toDTO(noteRepository.save(existing));
    }

    private Note.RoleJury determinerRole(Encadrant e, Jury jury) {
        if (e.getId().equals(jury.getPresident().getId()))   return Note.RoleJury.PRESIDENT;
        if (e.getId().equals(jury.getRapporteur().getId()))  return Note.RoleJury.RAPPORTEUR;
        return Note.RoleJury.EXAMINATEUR;
    }

    private Encadrant getEncadrantOrThrow(Long encadrantId) {
        return encadrantRepository.findById(encadrantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Encadrant introuvable avec l'id : " + encadrantId));
    }

    private NoteDTO toDTO(Note n) {
        NoteDTO dto = new NoteDTO();
        dto.setId(n.getId());
        dto.setSoutenanceId(n.getSoutenance().getId());
        dto.setEvaluateurId(n.getEvaluateur().getId());
        dto.setEvaluateurNom(n.getEvaluateur().getPrenom() + " " + n.getEvaluateur().getNom());
        dto.setRoleJury(n.getRoleJury());
        dto.setNoteExpose(n.getNoteExpose());
        dto.setNoteRapport(n.getNoteRapport());
        dto.setNoteQuestions(n.getNoteQuestions());
        dto.setCommentaire(n.getCommentaire());
        try {
            dto.setMoyenneEvaluateur(n.getMoyenneEvaluateur());
        } catch (IllegalStateException ignored) {}
        return dto;
    }
}


