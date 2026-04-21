package com.soutenance.orchestrator;

import com.soutenance.features.encadrant.entity.Encadrant;
import com.soutenance.features.encadrant.service.EncadrantService;
import com.soutenance.features.jury.entity.Jury;
import com.soutenance.features.jury.service.Interface.JuryService;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.service.SoutenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JuryOrchestrator {

    private final EncadrantService encadrantService;
    private final SoutenanceService soutenanceService;
    private final JuryService juryService;

    public Jury createJury(Long soutenanceId,
                           Long presidentId,
                           Long rapporteurId,
                           Long examinateurId) {

        Encadrant president = encadrantService.getOrThrow(presidentId);
        Encadrant rapporteur = encadrantService.getOrThrow(rapporteurId);
        Encadrant examinateur = encadrantService.getOrThrow(examinateurId);

        Soutenance soutenance = soutenanceService.getOrThrow(soutenanceId);

        if (presidentId.equals(rapporteurId)
                || presidentId.equals(examinateurId)
                || rapporteurId.equals(examinateurId)) {
            throw new RuntimeException("Les membres du jury doivent être différents");
        }

        Jury jury = new Jury();
        jury.setPresident(president);
        jury.setRapporteur(rapporteur);
        jury.setExaminateur(examinateur);
        jury.setSoutenance(soutenance);

        return juryService.save(jury);
    }
}
