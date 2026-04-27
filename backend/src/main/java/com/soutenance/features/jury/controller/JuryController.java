package com.soutenance.features.jury.controller;

import com.soutenance.features.jury.dto.JuryDTO;
import com.soutenance.features.soutenance.dto.SoutenanceDTO;
import com.soutenance.orchestrator.JuryOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jurys")
@RequiredArgsConstructor
public class JuryController {

    private final JuryOrchestrator juryOrchestrator;

    @PostMapping
    public SoutenanceDTO affecter(@RequestBody JuryDTO dto) {
        return juryOrchestrator.affecterJury(dto);
    }

    @GetMapping("/soutenance/{soutenanceId}")
    public JuryDTO getBySoutenance(@PathVariable Long soutenanceId) {
        return juryOrchestrator.getBySoutenance(soutenanceId);
    }

    @DeleteMapping("/soutenance/{soutenanceId}")
    public void delete(@PathVariable Long soutenanceId) {
        juryOrchestrator.deleteJury(soutenanceId);
    }
}
