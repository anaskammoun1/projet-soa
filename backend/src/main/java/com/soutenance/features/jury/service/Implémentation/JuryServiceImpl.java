package com.soutenance.features.jury.service.Implémentation;

import com.soutenance.exception.BusinessException;
import com.soutenance.exception.ConflitHoraireException;
import com.soutenance.exception.ResourceNotFoundException;
import com.soutenance.features.encadrant.entity.Encadrant;
import com.soutenance.features.encadrant.repository.EncadrantRepository;
import com.soutenance.features.jury.dto.JuryDTO;
import com.soutenance.features.jury.entity.Jury;
import com.soutenance.features.jury.repository.JuryRepository;
import com.soutenance.features.jury.service.Interface.JuryService;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.repository.SoutenanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class JuryServiceImpl implements JuryService {

    private final JuryRepository juryRepository;
    private final SoutenanceRepository soutenanceRepository;
    private final EncadrantRepository encadrantRepository;

    public JuryServiceImpl(JuryRepository juryRepository,
                           SoutenanceRepository soutenanceRepository,
                           EncadrantRepository encadrantRepository) {
        this.juryRepository = juryRepository;
        this.soutenanceRepository = soutenanceRepository;
        this.encadrantRepository = encadrantRepository;
    }

    @Override
    public JuryDTO findBySoutenance(Long soutenanceId) {
        Jury jury = juryRepository.findBySoutenanceId(soutenanceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun jury affecté à la soutenance id : " + soutenanceId));
        return toDTO(jury);
    }

    @Override
    @Transactional
    public JuryDTO affecter(JuryDTO dto) {
        Soutenance soutenance = soutenanceRepository.findById(dto.getSoutenanceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Soutenance introuvable : " + dto.getSoutenanceId()));

        if (soutenance.getStatut() == Soutenance.StatutSoutenance.TERMINEE) {
            throw new BusinessException("Impossible d'affecter un jury à une soutenance terminée.");
        }

        if (dto.getPresidentId().equals(dto.getRapporteurId())
                || dto.getPresidentId().equals(dto.getExaminateurId())
                || dto.getRapporteurId().equals(dto.getExaminateurId())) {
            throw new BusinessException("Les membres du jury doivent être des personnes différentes.");
        }

        Encadrant president = getEncadrantOrThrow(dto.getPresidentId());
        Encadrant rapporteur = getEncadrantOrThrow(dto.getRapporteurId());
        Encadrant examinateur = getEncadrantOrThrow(dto.getExaminateurId());

        LocalDateTime debut = soutenance.getDateHeure();
        LocalDateTime fin = debut.plusMinutes(soutenance.getDureeMinutes());

        verifierConflitMembreJury(president, debut, fin, soutenance.getId(), "Président");
        verifierConflitMembreJury(rapporteur, debut, fin, soutenance.getId(), "Rapporteur");
        verifierConflitMembreJury(examinateur, debut, fin, soutenance.getId(), "Examinateur");

        juryRepository.findBySoutenanceId(dto.getSoutenanceId())
                .ifPresent(juryRepository::delete);

        Jury jury = new Jury();
        jury.setSoutenance(soutenance);
        jury.setPresident(president);
        jury.setRapporteur(rapporteur);
        jury.setExaminateur(examinateur);

        return toDTO(juryRepository.save(jury));
    }

    @Override
    @Transactional
    public void delete(Long soutenanceId) {
        Jury jury = juryRepository.findBySoutenanceId(soutenanceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun jury pour la soutenance id : " + soutenanceId));
        juryRepository.delete(jury);
    }

    @Override
    @Transactional
    public Jury save(Jury jury) {
        return juryRepository.save(jury);
    }

    private Encadrant getEncadrantOrThrow(Long encadrantId) {
        return encadrantRepository.findById(encadrantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Encadrant introuvable avec l'id : " + encadrantId));
    }

    private void verifierConflitMembreJury(Encadrant membre, LocalDateTime debut,
                                           LocalDateTime fin, Long soutenanceId, String role) {
        boolean conflit = soutenanceRepository.existsConflitEncadrant(
                membre.getId(), debut, fin, soutenanceId);
        if (conflit) {
            throw new ConflitHoraireException(
                    role + " " + membre.getPrenom() + " " + membre.getNom()
                            + " a déjà une soutenance en tant qu'encadrant sur ce créneau."
            );
        }
    }

    private JuryDTO toDTO(Jury jury) {
        JuryDTO dto = new JuryDTO();
        dto.setId(jury.getId());
        dto.setSoutenanceId(jury.getSoutenance().getId());
        dto.setPresidentId(jury.getPresident().getId());
        dto.setPresidentNom(jury.getPresident().getPrenom() + " " + jury.getPresident().getNom());
        dto.setRapporteurId(jury.getRapporteur().getId());
        dto.setRapporteurNom(jury.getRapporteur().getPrenom() + " " + jury.getRapporteur().getNom());
        dto.setExaminateurId(jury.getExaminateur().getId());
        dto.setExaminateurNom(jury.getExaminateur().getPrenom() + " " + jury.getExaminateur().getNom());
        return dto;
    }
}
