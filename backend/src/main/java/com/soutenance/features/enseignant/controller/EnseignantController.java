package com.soutenance.features.enseignant.controller;

import com.soutenance.features.enseignant.dto.EnseignantDTO;
import com.soutenance.features.enseignant.service.Implementation.EnseignantServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enseignants")
@CrossOrigin("*")
public class EnseignantController {

    private final EnseignantServiceImpl service;

    public EnseignantController(EnseignantServiceImpl service) {
        this.service = service;
    }

    @PostMapping
    public EnseignantDTO create(@RequestBody EnseignantDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<EnseignantDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EnseignantDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public EnseignantDTO update(
            @PathVariable Long id,
            @RequestBody EnseignantDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}