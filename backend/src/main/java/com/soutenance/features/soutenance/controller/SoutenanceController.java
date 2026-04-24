package com.soutenance.features.soutenance.controller;

import com.soutenance.features.soutenance.dto.SoutenanceDTO;
import com.soutenance.features.soutenance.service.Implementation.SoutenanceServiceImpl;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/soutenances")
@CrossOrigin("*")
public class SoutenanceController {

    private final SoutenanceServiceImpl service;

    public SoutenanceController(SoutenanceServiceImpl service) {
        this.service = service;
    }

    @PostMapping
    public SoutenanceDTO create(@RequestBody SoutenanceDTO dto) {

        return service.create(dto);
    }

    @GetMapping
    public List<SoutenanceDTO> getAll() {

        return service.getAll();
    }

    @GetMapping("/{id}")
    public SoutenanceDTO getById(@PathVariable Long id) {

        return service.getById(id);
    }

    @PutMapping("/{id}")
    public SoutenanceDTO update(
            @PathVariable Long id,
            @RequestBody SoutenanceDTO dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {

        service.delete(id);
    }
}