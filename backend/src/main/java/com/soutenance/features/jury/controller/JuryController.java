package com.soutenance.features.jury.controller;

import com.soutenance.features.jury.dto.JuryDTO;
import com.soutenance.features.jury.service.Interface.JuryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jurys")
public class JuryController {

    private final JuryService juryService;

    public JuryController(JuryService juryService) {
        this.juryService = juryService;
    }

    @GetMapping("/soutenance/{soutenanceId}")
    public JuryDTO findBySoutenance(@PathVariable Long soutenanceId) {
        return juryService.findBySoutenance(soutenanceId);
    }

    @PostMapping
    public JuryDTO affecter(@Valid @RequestBody JuryDTO dto) {
        return juryService.affecter(dto);
    }

    @DeleteMapping("/soutenance/{soutenanceId}")
    public void delete(@PathVariable Long soutenanceId) {
        juryService.delete(soutenanceId);
    }
}


