package com.soutenance.features.note.controller;

import com.soutenance.features.note.dto.NoteDTO;
import com.soutenance.orchestrator.NotationOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NotationOrchestrator notationOrchestrator;

    @PostMapping
    @PreAuthorize("@ownershipSecurity.canEvaluate(#dto.evaluateurId)")
    public NoteDTO saisir(@RequestBody NoteDTO dto) {
        return notationOrchestrator.saisirNote(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ownershipSecurity.canEvaluate(#dto.evaluateurId)")
    public NoteDTO modifier(@PathVariable Long id, @RequestBody NoteDTO dto) {
        return notationOrchestrator.modifierNote(id, dto);
    }

    @GetMapping("/soutenance/{soutenanceId}")
    @PreAuthorize("@ownershipSecurity.canAccessNotes(#soutenanceId)")
    public List<NoteDTO> getBySoutenance(@PathVariable Long soutenanceId) {
        return notationOrchestrator.getNotesBySoutenance(soutenanceId);
    }
}
