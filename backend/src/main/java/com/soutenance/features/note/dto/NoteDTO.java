package com.soutenance.features.note.dto;

import com.soutenance.features.note.entity.Note;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoteDTO {
    private Long id;

    @NotNull(message = "La soutenance est obligatoire")
    private Long soutenanceId;

    @NotNull(message = "L'évaluateur est obligatoire")
    private Long evaluateurId;

    @NotNull(message = "Le rôle dans le jury est obligatoire")
    private Note.RoleJury roleJury;

    @NotNull
    @DecimalMin("0.0") @DecimalMax("20.0")
    private Double noteExpose;

    @NotNull
    @DecimalMin("0.0") @DecimalMax("20.0")
    private Double noteRapport;

    @NotNull
    @DecimalMin("0.0") @DecimalMax("20.0")
    private Double noteQuestions;

    private String commentaire;

    // Réponse
    private Double moyenneEvaluateur;
    private String evaluateurNom;
}


