package com.soutenance.features.salle.controller;

import com.soutenance.features.salle.dto.SalleDTO;
import com.soutenance.features.salle.service.Implementation.SalleServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salles")
@CrossOrigin("*")
public class SalleController {

    private final SalleServiceImpl service;

    public SalleController(SalleServiceImpl service) {
        this.service = service;
    }

    @PostMapping
    public SalleDTO create(@RequestBody SalleDTO dto) {

        return service.create(dto);
    }

    @GetMapping
    public List<SalleDTO> getAll() {

        return service.getAll();
    }

    @GetMapping("/{id}")
    public SalleDTO getById(@PathVariable Long id) {

        return service.getById(id);
    }

    @PutMapping("/{id}")
    public SalleDTO update(
            @PathVariable Long id,
            @RequestBody SalleDTO dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {

        service.delete(id);
    }
}