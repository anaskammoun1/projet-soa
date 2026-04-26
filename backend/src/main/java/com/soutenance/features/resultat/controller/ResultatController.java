package com.soutenance.features.resultat.controller;

import com.soutenance.features.resultat.dto.ResultatDTO;
import com.soutenance.features.resultat.service.ResultatService;
import com.soutenance.features.resultat.service.ResultatService.ResultatStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resultats")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ResultatController {

    private final ResultatService resultatService;

    @GetMapping
    public ResponseEntity<List<ResultatDTO>> getAllResultats() {
        List<ResultatDTO> resultats = resultatService.getAllResultats().stream()
                .map(ResultatDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultatDTO> getResultatById(@PathVariable Long id) {
        Optional<ResultatDTO> resultat = resultatService.getResultatById(id).map(ResultatDTO::fromEntity);
        return resultat.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/soutenance/{soutenanceId}")
    public ResponseEntity<ResultatDTO> getResultatBySoutenanceId(@PathVariable Long soutenanceId) {
        Optional<ResultatDTO> resultat = resultatService.getResultatBySoutenanceId(soutenanceId).map(ResultatDTO::fromEntity);
        return resultat.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/published")
    public ResponseEntity<List<ResultatDTO>> getPublishedResultats() {
        List<ResultatDTO> resultats = resultatService.getPublishedResultats().stream()
                .map(ResultatDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultats);
    }

    @PostMapping("/calculate")
    public ResponseEntity<ResultatDTO> calculateResultat(@RequestParam Long soutenanceId, @RequestParam Long etudiantId) {
        ResultatDTO resultat = ResultatDTO.fromEntity(resultatService.calculateResultat(soutenanceId, etudiantId));
        return ResponseEntity.status(HttpStatus.CREATED).body(resultat);
    }

    @PutMapping("/{id}/validate")
    public ResponseEntity<ResultatDTO> validateResultat(@PathVariable Long id) {
        ResultatDTO resultat = ResultatDTO.fromEntity(resultatService.validateResultat(id));
        return ResponseEntity.ok(resultat);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<ResultatDTO> publishResultat(@PathVariable Long id) {
        try {
            ResultatDTO resultat = ResultatDTO.fromEntity(resultatService.publishResultat(id));
            return ResponseEntity.ok(resultat);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ResultatStatistics> getStatistics() {
        ResultatStatistics statistics = resultatService.getStatistics();
        return ResponseEntity.ok(statistics);
    }
}
