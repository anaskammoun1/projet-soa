package com.soutenance.features.resultat.dto;

import com.soutenance.features.resultat.entity.Resultat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultatDTO {

    private Long id;
    private Long etudiantId;
    private Long soutenanceId;
    private Double moyenneFinale;
    private String mention;
    private String decisionFinale;
    private Boolean valide;
    private Boolean publie;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    public static ResultatDTO fromEntity(Resultat resultat) {
        if (resultat == null) {
            return null;
        }

        return ResultatDTO.builder()
                .id(resultat.getId())
                .etudiantId(resultat.getEtudiantId())
                .soutenanceId(resultat.getSoutenanceId())
                .moyenneFinale(resultat.getMoyenneFinale())
                .mention(resultat.getMention() != null ? resultat.getMention().name() : null)
                .decisionFinale(resultat.getDecisionFinale() != null ? resultat.getDecisionFinale().name() : null)
                .valide(resultat.getValide())
                .publie(resultat.getPublie())
                .createdAt(resultat.getCreatedAt())
                .updatedAt(resultat.getUpdatedAt())
                .publishedAt(resultat.getPublishedAt())
                .build();
    }
}
