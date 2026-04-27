package com.soutenance.features.note.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {

    private Long id;
    private Long soutenanceId;
    private Long evaluateurId;
    private String roleJury;
    private Double noteExpose;
    private Double noteRapport;
    private Double noteQuestions;
    private Double moyenneEvaluateur;
}