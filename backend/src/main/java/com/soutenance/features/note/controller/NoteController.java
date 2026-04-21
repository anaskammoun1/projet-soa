package com.soutenance.features.note.controller;

import com.soutenance.features.note.dto.NoteDTO;
import com.soutenance.features.note.service.Interface.NoteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/soutenance/{soutenanceId}")
    public List<NoteDTO> findBySoutenance(@PathVariable Long soutenanceId) {
        return noteService.findBySoutenance(soutenanceId);
    }

    @PostMapping
    public NoteDTO saisirNote(@Valid @RequestBody NoteDTO dto) {
        return noteService.saisirNote(dto);
    }

    @PutMapping("/{id}")
    public NoteDTO modifierNote(@PathVariable Long id, @Valid @RequestBody NoteDTO dto) {
        return noteService.modifierNote(id, dto);
    }
}


