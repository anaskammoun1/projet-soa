package com.soutenance.features.etudiant.controller;

import com.soutenance.features.etudiant.dto.EtudiantDTO;
import com.soutenance.features.etudiant.service.Interface.EtudiantService;
import com.soutenance.security.CurrentUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final EtudiantService service;
    private final CurrentUserService currentUserService;

    public EtudiantController(EtudiantService service, CurrentUserService currentUserService) {
        this.service = service;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public EtudiantDTO create(@RequestBody EtudiantDTO dto) {
        return service.createEtudiant(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ownershipSecurity.canAccessEtudiant(#id)")
    public EtudiantDTO getById(@PathVariable Integer id) {
        return service.getEtudiantById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ETUDIANT')")
    public EtudiantDTO me() {
        return service.getEtudiantById(currentUserService.getCurrentEtudiantId());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<EtudiantDTO> getAll() {
        return service.getAllEtudiants();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EtudiantDTO update(@PathVariable Integer id, @RequestBody EtudiantDTO dto) {
        return service.updateEtudiant(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Integer id) {
        service.deleteEtudiant(id);
    }

    @GetMapping("/exists/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean exists(@PathVariable Integer id) {
        return service.existsEtudiant(id);
    }
}
