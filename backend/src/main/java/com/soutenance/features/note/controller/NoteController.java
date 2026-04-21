package com.soutenance.features.note.controller;

import com.soutenance.features.note.dto.NoteDTO;
import com.soutenance.features.note.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    // ── Consulter toutes les notes d'une soutenance ────────────────────────
    @GetMapping("/soutenance/{soutenanceId}")
    public ResponseEntity<List<NoteDTO>> findBySoutenance(@PathVariable Long soutenanceId) {
        return ResponseEntity.ok(noteService.findBySoutenance(soutenanceId));
    }

    // ── Saisir une note (par un membre du jury) ────────────────────────────
    @PostMapping
    public ResponseEntity<NoteDTO> saisirNote(@Valid @RequestBody NoteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.saisirNote(dto));
    }

    // ── Modifier une note existante ────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> modifierNote(@PathVariable Long id,
                                                 @Valid @RequestBody NoteDTO dto) {
        return ResponseEntity.ok(noteService.modifierNote(id, dto));
    }
}


