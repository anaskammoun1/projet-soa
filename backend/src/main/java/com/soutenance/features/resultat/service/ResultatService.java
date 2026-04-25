package com.soutenance.features.resultat.service;

import com.soutenance.features.resultat.entity.Resultat;
import com.soutenance.features.resultat.entity.Resultat.Mention;
import com.soutenance.features.resultat.entity.Resultat.Decision;
import com.soutenance.features.resultat.repository.ResultatRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResultatService {

    private final ResultatRepository resultatRepository;

    public Resultat calculateResultat(Long soutenanceId, Long etudiantId) {
        if (resultatRepository.existsBySoutenanceId(soutenanceId)) {
            throw new RuntimeException("Result already exists for this soutenance");
        }

        List<Double> notes = List.of(14.0, 15.0, 13.0);
        double moyenne = calculateMoyenne(notes);
        Mention mention = attributeMention(moyenne);
        Decision decision = determineDecision(moyenne);

        Resultat resultat = Resultat.builder()
                .etudiantId(etudiantId)
                .soutenanceId(soutenanceId)
                .moyenneFinale(moyenne)
                .mention(mention)
                .decisionFinale(decision)
                .valide(false)
                .publie(false)
                .build();

        return resultatRepository.save(resultat);
    }

    public List<Resultat> getAllResultats() {
        return resultatRepository.findAll();
    }

    public Optional<Resultat> getResultatById(Long id) {
        return resultatRepository.findById(id);
    }

    public Optional<Resultat> getResultatBySoutenanceId(Long soutenanceId) {
        return resultatRepository.findBySoutenanceId(soutenanceId);
    }

    public List<Resultat> getPublishedResultats() {
        return resultatRepository.findByPublieTrue();
    }

    public Resultat validateResultat(Long id) {
        Resultat resultat = resultatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found"));
        resultat.setValide(true);
        return resultatRepository.save(resultat);
    }

    public Resultat publishResultat(Long id) {
        Resultat resultat = resultatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found"));

        if (!Boolean.TRUE.equals(resultat.getValide())) {
            throw new RuntimeException("Cannot publish unvalidated result");
        }

        resultat.setPublie(true);
        resultat.setPublishedAt(LocalDateTime.now());
        return resultatRepository.save(resultat);
    }

    public ResultatStatistics getStatistics() {
        long totalResults = resultatRepository.count();
        long admis = resultatRepository.countAdmis();
        long ajourne = resultatRepository.countAjourne();
        long published = resultatRepository.countPublished();
        double admisPercentage = published > 0 ? (double) admis / published * 100 : 0;

        return new ResultatStatistics(totalResults, admis, ajourne, published, admisPercentage);
    }

    private double calculateMoyenne(List<Double> notes) {
        return notes.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private Mention attributeMention(Double moyenne) {
        if (moyenne < 10) return Mention.SANS_MENTION;
        if (moyenne < 12) return Mention.PASSABLE;
        if (moyenne < 14) return Mention.ASSEZ_BIEN;
        if (moyenne < 16) return Mention.BIEN;
        return Mention.TRES_BIEN;
    }

    private Decision determineDecision(Double moyenne) {
        return moyenne >= 10 ? Decision.ADMIS : Decision.AJOURNE;
    }

    @Data
    @RequiredArgsConstructor
    public static class ResultatStatistics {
        private final long totalResults;
        private final long admis;
        private final long ajourne;
        private final long published;
        private final double admisPercentage;
    }
}
