package com.soutenance.features.note.service.Interface;

import com.soutenance.features.note.dto.NoteDTO;
import com.soutenance.features.note.entity.Note;

import java.util.List;

public interface NoteService {

    List<NoteDTO> findBySoutenance(Long soutenanceId);

    boolean existsBySoutenanceAndEvaluateur(Long soutenanceId, Long evaluateurId);

    NoteDTO saisirNote(NoteDTO dto);

    Note save(Note note);

    NoteDTO modifierNote(Long noteId, NoteDTO dto);
}
