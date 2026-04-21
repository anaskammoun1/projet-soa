package com.soutenance.features.jury.controller;

import com.soutenance.features.jury.dto.JuryDTO;
import com.soutenance.features.jury.service.JuryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jurys")
@RequiredArgsConstructor
public class JuryController {

    private final JuryService juryService;

    // ── Consulter le jury d'une soutenance ─────────────────────────────────
    @GetMapping("/soutenance/{soutenanceId}")
    public ResponseEntity<JuryDTO> findBySoutenance(@PathVariable Long soutenanceId) {
        return ResponseEntity.ok(juryService.findBySoutenance(soutenanceId));
    }

    // ── Affecter (ou remplacer) le jury d'une soutenance ──────────────────
    @PostMapping
    public ResponseEntity<JuryDTO> affecter(@Valid @RequestBody JuryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(juryService.affecter(dto));
    }

    // ── Supprimer l'affectation du jury ────────────────────────────────────
    @DeleteMapping("/soutenance/{soutenanceId}")
    public ResponseEntity<Void> delete(@PathVariable Long soutenanceId) {
        juryService.delete(soutenanceId);
        return ResponseEntity.noContent().build();
    }
}


