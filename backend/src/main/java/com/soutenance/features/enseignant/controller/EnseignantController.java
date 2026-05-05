package com.soutenance.features.enseignant.controller;

import com.soutenance.features.enseignant.dto.EnseignantDTO;
import com.soutenance.features.enseignant.service.Implementation.EnseignantServiceImpl;
import com.soutenance.security.CurrentUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/enseignants", "/api/encadrants"})
@CrossOrigin("*")
public class EnseignantController {

    private final EnseignantServiceImpl service;
    private final CurrentUserService currentUserService;

    public EnseignantController(EnseignantServiceImpl service, CurrentUserService currentUserService) {
        this.service = service;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public EnseignantDTO create(@RequestBody EnseignantDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<EnseignantDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public EnseignantDTO me() {
        return service.getById(currentUserService.getCurrentEnseignantId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EnseignantDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EnseignantDTO update(
            @PathVariable Long id,
            @RequestBody EnseignantDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
