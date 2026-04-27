package com.soutenance.features.resultat.controller;

import com.soutenance.features.resultat.dto.ResultatDTO;
import com.soutenance.features.resultat.service.ResultatService;
import com.soutenance.features.resultat.service.ResultatService.ResultatStatistics;
import com.soutenance.orchestrator.NotationOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final NotationOrchestrator notationOrchestrator;

    @GetMapping
    public ResponseEntity<List<ResultatDTO>> getAllResultats() {
        List<ResultatDTO> resultats = resultatService.getAllResultats().stream()
                .map(ResultatDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
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

    @GetMapping("/publies")
    public ResponseEntity<List<ResultatDTO>> getPublishedResultatsLegacy() {
        return getPublishedResultats();
    }

    @PostMapping("/calculate")
    public ResponseEntity<ResultatDTO> calculateResultat(@RequestParam Long soutenanceId, @RequestParam Long etudiantId) {
        ResultatDTO resultat = ResultatDTO.fromEntity(notationOrchestrator.calculerResultat(soutenanceId, etudiantId));
        return ResponseEntity.status(HttpStatus.CREATED).body(resultat);
    }

    @PostMapping("/soutenance/{soutenanceId}/calculer")
    public ResponseEntity<ResultatDTO> calculateResultatBySoutenance(@PathVariable Long soutenanceId) {
        ResultatDTO resultat = ResultatDTO.fromEntity(notationOrchestrator.calculerResultat(soutenanceId));
        return ResponseEntity.status(HttpStatus.CREATED).body(resultat);
    }

    @PutMapping("/{id}/validate")
    public ResponseEntity<ResultatDTO> validateResultat(@PathVariable Long id) {
        ResultatDTO resultat = ResultatDTO.fromEntity(resultatService.validateResultat(id));
        return ResponseEntity.ok(resultat);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<ResultatDTO> publishResultat(@PathVariable Long id) {
        ResultatDTO resultat = ResultatDTO.fromEntity(resultatService.publishResultat(id));
        return ResponseEntity.ok(resultat);
    }

    @PutMapping("/soutenance/{soutenanceId}/publier")
    public ResponseEntity<ResultatDTO> publishResultatBySoutenance(@PathVariable Long soutenanceId) {
        ResultatDTO resultat = resultatService.getResultatBySoutenanceId(soutenanceId)
                .map(result -> ResultatDTO.fromEntity(resultatService.publishResultat(result.getId())))
                .orElseThrow(() -> new RuntimeException("Resultat non trouvé pour cette soutenance"));
        return ResponseEntity.ok(resultat);
    }

    @GetMapping("/etudiant/{etudiantId}")
    @PreAuthorize("@ownershipSecurity.canAccessEtudiant(#etudiantId)")
    public ResponseEntity<ResultatDTO> getResultatByEtudiant(@PathVariable Long etudiantId) {
        return resultatService.getPublishedResultatByEtudiantId(etudiantId)
                .map(ResultatDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/stats")
    public ResponseEntity<ResultatStatistics> getStatistics() {
        ResultatStatistics statistics = resultatService.getStatistics();
        return ResponseEntity.ok(statistics);
    }
}
