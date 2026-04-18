package com.soutenance.orchestrator;

import com.soutenance.features.encadrant.entity.Encadrant;
import com.soutenance.features.encadrant.service.EncadrantService;
import com.soutenance.features.etudiant.entity.Etudiant;
import com.soutenance.features.etudiant.service.EtudiantService;
import com.soutenance.features.salle.entity.Salle;
import com.soutenance.features.salle.service.SalleService;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.service.SoutenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PlanificationOrchestrator {

    private final EtudiantService etudiantService;
    private final EncadrantService encadrantService;
    private final SalleService salleService;
    private final SoutenanceService soutenanceService;

    public Soutenance planifierSoutenance(Soutenance s) {

        Etudiant etudiant = etudiantService.getOrThrow(s.getEtudiant().getId());
        Encadrant encadrant = encadrantService.getOrThrow(s.getEncadrant().getId());
        Salle salle = salleService.getOrThrow(s.getSalle().getId());

        LocalDateTime debut = s.getDateHeure();
        LocalDateTime fin = debut.plusMinutes(s.getDureeMinutes());

        if (!salle.isDisponible()) {
            throw new RuntimeException("Salle non disponible");
        }

        if (soutenanceService.existsConflitSalle(salle.getId(), debut, fin)) {
            throw new RuntimeException("Conflit de salle");
        }

        if (soutenanceService.existsConflitEncadrant(encadrant.getId(), debut, fin, null)) {
            throw new RuntimeException("Encadrant occupé");
        }

        s.setEtudiant(etudiant);
        s.setEncadrant(encadrant);
        s.setSalle(salle);

        return soutenanceService.save(s);
    }
}