package com.soutenance.features.jury.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JuryDTO {
    private Long id;
    private Long soutenanceId;

    @NotNull(message = "Le président est obligatoire")
    private Long presidentId;

    @NotNull(message = "Le rapporteur est obligatoire")
    private Long rapporteurId;

    @NotNull(message = "L'examinateur est obligatoire")
    private Long examinateurId;

    // Pour la réponse enrichie
    private String presidentNom;
    private String rapporteurNom;
    private String examinateurNom;
}


