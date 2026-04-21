package com.soutenance.orchestrator;

import com.soutenance.features.encadrant.entity.Encadrant;
import com.soutenance.features.encadrant.service.EncadrantService;
import com.soutenance.features.note.entity.Note;
import com.soutenance.features.note.service.Interface.NoteService;
import com.soutenance.features.resultat.service.ResultatService;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.service.SoutenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotationOrchestrator {

    private final EncadrantService encadrantService;
    private final SoutenanceService soutenanceService;
    private final NoteService noteService;
    private final ResultatService resultatService;

    public void noterSoutenance(Long soutenanceId,
                                 Long evaluateurId,
                                 double noteRapport,
                                 double noteExpose,
                                 double noteQuestions) {

        if (noteRapport < 0 || noteRapport > 20) {
            throw new RuntimeException("Note invalide");
        }

        Encadrant evaluateur = encadrantService.getOrThrow(evaluateurId);
        Soutenance soutenance = soutenanceService.getOrThrow(soutenanceId);

        if (noteService.existsBySoutenanceAndEvaluateur(soutenance.getId(), evaluateur.getId())) {
            throw new RuntimeException("Cet evaluateur a deja note cette soutenance");
        }

        Note note = new Note();
        note.setEvaluateur(evaluateur);
        note.setSoutenance(soutenance);
        note.setNoteRapport(noteRapport);
        note.setNoteExpose(noteExpose);
        note.setNoteQuestions(noteQuestions);

        noteService.save(note);

        resultatService.calculerMoyenne(soutenanceId);
    }
}