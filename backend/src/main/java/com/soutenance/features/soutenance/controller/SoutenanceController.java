package com.soutenance.features.soutenance.controller;

import com.soutenance.features.soutenance.dto.SoutenanceDTO;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.entity.StatutSoutenance;
import com.soutenance.features.soutenance.service.Interface.SoutenanceService;
import com.soutenance.orchestrator.PlanificationOrchestrator;
import com.soutenance.orchestrator.SoutenanceReadOrchestrator;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/soutenances")
@CrossOrigin("*")
public class SoutenanceController {

    private final SoutenanceService service;
    private final PlanificationOrchestrator planificationOrchestrator;
    private final SoutenanceReadOrchestrator soutenanceReadOrchestrator;

    public SoutenanceController(SoutenanceService service,
                                PlanificationOrchestrator planificationOrchestrator,
                                SoutenanceReadOrchestrator soutenanceReadOrchestrator) {
        this.service = service;
        this.planificationOrchestrator = planificationOrchestrator;
        this.soutenanceReadOrchestrator = soutenanceReadOrchestrator;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SoutenanceDTO create(@RequestBody SoutenanceDTO dto) {
        Soutenance soutenance = planificationOrchestrator.planifierSoutenance(dto);
        return service.getById(soutenance.getId());
    }

    @GetMapping
    public List<SoutenanceDTO> getAll() {
        return soutenanceReadOrchestrator.getVisibleSoutenances();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ownershipSecurity.canAccessSoutenance(#id)")
    public SoutenanceDTO getById(@PathVariable Long id) {

        return service.getById(id);
    }

    @GetMapping("/{id}/debug")
    @PreAuthorize("@ownershipSecurity.canAccessSoutenance(#id)")
    public Map<String, Object> debugStatus(@PathVariable Long id) {
        SoutenanceDTO dto = service.getById(id);
        LocalDateTime now = LocalDateTime.now();
        
        Map<String, Object> debug = new LinkedHashMap<>();
        debug.put("id", dto.getId());
        debug.put("titre", dto.getTitre());
        debug.put("date", dto.getDate());
        debug.put("duree", dto.getDuree());
        debug.put("currentStatut", dto.getStatut());
        debug.put("serverNow", now);
        
        if (dto.getDate() != null) {
            LocalDateTime debut = dto.getDate();
            LocalDateTime fin = debut.plusMinutes(dto.getDuree());
            debug.put("startTime", debut);
            debug.put("endTime", fin);
            debug.put("isStarted", now.isAfter(debut) || now.isEqual(debut));
            debug.put("isFinished", now.isAfter(fin) || now.isEqual(fin));
            
            String computedStatus = "ERROR";
            if (now.isBefore(debut)) {
                computedStatus = "PLANIFIEE (future)";
            } else if (now.isBefore(fin)) {
                computedStatus = "EN_COURS (in progress)";
            } else {
                computedStatus = "TERMINEE (past)";
            }
            debug.put("computedStatus", computedStatus);
        }
        
        return debug;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SoutenanceDTO update(
            @PathVariable Long id,
            @RequestBody SoutenanceDTO dto) {
        Soutenance soutenance = planificationOrchestrator.modifierSoutenance(id, dto);
        return service.getById(soutenance.getId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {

        service.delete(id);
    }

    @GetMapping("/etudiant/{etudiantId}")
    @PreAuthorize("@ownershipSecurity.canAccessEtudiant(#etudiantId)")
    public List<SoutenanceDTO> getByEtudiant(@PathVariable Integer etudiantId) {
        return service.getByEtudiantId(etudiantId);
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    public SoutenanceDTO updateStatut(@PathVariable Long id, @RequestParam StatutSoutenance statut) {
        return service.updateStatut(id, statut);
    }
}
