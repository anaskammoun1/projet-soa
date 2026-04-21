package com.soutenance.features.encadrant.controller;
import com.soutenance.features.encadrant.dto.EncadrantDTO;
import com.soutenance.features.encadrant.service.Interface.EncadrantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encadrants")
public class EncadrantController {

    private final EncadrantService service;

    public EncadrantController(EncadrantService service) {
        this.service = service;
    }

    @PostMapping
    public EncadrantDTO create(@RequestBody EncadrantDTO dto) {
        return service.createEncadrant(dto);
    }

    @GetMapping("/{id}")
    public EncadrantDTO getById(@PathVariable Long id) {
        return service.getEncadrantById(id);
    }

    @GetMapping
    public List<EncadrantDTO> getAll() {
        return service.getAllEncadrants();
    }

    @PutMapping("/{id}")
    public EncadrantDTO update(@PathVariable Long id, @RequestBody EncadrantDTO dto) {
        return service.updateEncadrant(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteEncadrant(id);
    }

    @GetMapping("/exists/{id}")
    public boolean exists(@PathVariable Long id) {
        return service.existsEncadrant(id);
    }
}