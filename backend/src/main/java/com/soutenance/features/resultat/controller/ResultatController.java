package com.soutenance.features.resultat.controller;

import com.soutenance.features.resultat.dto.ResultatDTO;
import com.soutenance.features.resultat.service.ResultatService.ResultatStatistics;
import com.soutenance.orchestrator.NotationOrchestrator;
import com.soutenance.orchestrator.ResultatOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resultats")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ResultatController {

    private final ResultatOrchestrator resultatOrchestrator;
    private final NotationOrchestrator notationOrchestrator;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ResultatDTO>> getAllResultats() {
        List<ResultatDTO> resultats = resultatOrchestrator.getAllResultats().stream()
                .map(ResultatDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ownershipSecurity.canAccessResultat(#id)")
    public ResponseEntity<ResultatDTO> getResultatById(@PathVariable Long id) {
        return resultatOrchestrator.getResultatById(id)
                .map(ResultatDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/soutenance/{soutenanceId}")
    @PreAuthorize("@ownershipSecurity.canAccessResultatBySoutenance(#soutenanceId)")
    public ResponseEntity<ResultatDTO> getResultatBySoutenanceId(@PathVariable Long soutenanceId) {
        return resultatOrchestrator.getResultatBySoutenanceId(soutenanceId)
                .map(ResultatDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/published")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public ResponseEntity<List<ResultatDTO>> getPublishedResultats() {
        List<ResultatDTO> resultats = resultatOrchestrator.getVisiblePublishedResultats().stream()
                .map(ResultatDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/publies")
    public ResponseEntity<List<ResultatDTO>> getPublishedResultatsLegacy() {
        return getPublishedResultats();
    }

    @PostMapping("/calculate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultatDTO> calculateResultat(@RequestParam Long soutenanceId, @RequestParam Long etudiantId) {
        ResultatDTO resultat = ResultatDTO.fromEntity(notationOrchestrator.calculerResultat(soutenanceId, etudiantId));
        return ResponseEntity.status(HttpStatus.CREATED).body(resultat);
    }

    @PostMapping("/soutenance/{soutenanceId}/calculer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultatDTO> calculateResultatBySoutenance(@PathVariable Long soutenanceId) {
        ResultatDTO resultat = ResultatDTO.fromEntity(notationOrchestrator.calculerResultat(soutenanceId));
        return ResponseEntity.status(HttpStatus.CREATED).body(resultat);
    }

    @PutMapping("/{id}/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultatDTO> validateResultat(@PathVariable Long id) {
        ResultatDTO resultat = ResultatDTO.fromEntity(resultatOrchestrator.validateResultat(id));
        return ResponseEntity.ok(resultat);
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultatDTO> publishResultat(@PathVariable Long id) {
        ResultatDTO resultat = ResultatDTO.fromEntity(resultatOrchestrator.publishResultat(id));
        return ResponseEntity.ok(resultat);
    }

    @PutMapping("/soutenance/{soutenanceId}/publier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultatDTO> publishResultatBySoutenance(@PathVariable Long soutenanceId) {
        ResultatDTO resultat = ResultatDTO.fromEntity(resultatOrchestrator.publishResultatBySoutenance(soutenanceId));
        return ResponseEntity.ok(resultat);
    }

    @GetMapping("/etudiant/{etudiantId}")
    @PreAuthorize("@ownershipSecurity.canAccessEtudiant(#etudiantId)")
    public ResponseEntity<ResultatDTO> getResultatByEtudiant(@PathVariable Long etudiantId) {
        return resultatOrchestrator.getPublishedResultatByEtudiant(etudiantId)
                .map(ResultatDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<ResultatDTO> getMyResultat() {
        return resultatOrchestrator.getMyPublishedResultat()
                .map(ResultatDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultatStatistics> getStatistics() {
        ResultatStatistics statistics = resultatOrchestrator.getStatistics();
        return ResponseEntity.ok(statistics);
    }
}
