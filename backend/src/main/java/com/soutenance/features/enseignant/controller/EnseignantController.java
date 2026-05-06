package com.soutenance.features.enseignant.controller;

import com.soutenance.features.enseignant.dto.EnseignantDTO;
import com.soutenance.features.enseignant.service.Implementation.EnseignantServiceImpl;
import com.soutenance.security.CurrentUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/enseignants", "/api/encadrants"})
@CrossOrigin("*")
public class EnseignantController {

    private static final Logger logger = LoggerFactory.getLogger(EnseignantController.class);

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

    @GetMapping("/jury/available")
    public List<EnseignantDTO> getAvailableForJury(@RequestParam(required = false) Integer etudiantId) {
        logger.info("getAvailableForJury called with etudiantId: {}", etudiantId);
        try {
            if (etudiantId == null) {
                logger.warn("etudiantId is null, returning all enseignants");
                return service.getAll();
            }
            List<EnseignantDTO> result = service.getAvailableForJury(etudiantId);
            logger.info("Returning {} enseignants for etudiantId: {}", result.size(), etudiantId);
            return result;
        } catch (Exception e) {
            logger.error("Error in getAvailableForJury with etudiantId: {}", etudiantId, e);
            throw e;
        }
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
