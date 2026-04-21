package com.soutenance.features.etudiant.controller;

import com.soutenance.features.etudiant.dto.EtudiantDTO;
import com.soutenance.features.etudiant.service.Interface.EtudiantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final EtudiantService service;

    public EtudiantController(EtudiantService service) {
        this.service = service;
    }

    @PostMapping
    public EtudiantDTO create(@RequestBody EtudiantDTO dto) {
        return service.createEtudiant(dto);
    }

    @GetMapping("/{id}")
    public EtudiantDTO getById(@PathVariable Integer id) {
        return service.getEtudiantById(id);
    }

    @GetMapping
    public List<EtudiantDTO> getAll() {
        return service.getAllEtudiants();
    }

    @PutMapping("/{id}")
    public EtudiantDTO update(@PathVariable Integer id, @RequestBody EtudiantDTO dto) {
        return service.updateEtudiant(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.deleteEtudiant(id);
    }

    @GetMapping("/exists/{id}")
    public boolean exists(@PathVariable Integer id) {
        return service.existsEtudiant(id);
    }
}