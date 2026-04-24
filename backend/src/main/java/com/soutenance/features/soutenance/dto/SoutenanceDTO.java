package com.soutenance.features.soutenance.dto;

import com.soutenance.features.soutenance.entity.StatutSoutenance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoutenanceDTO {

    private Long id;

    private String titre;

    private LocalDateTime date;

    private int duree;

    private StatutSoutenance statut;

    private Long presidentId;

    private Long rapporteurId;

    private Long examinateurId;

    private Long salleId;

    private Integer etudiantId;

    private Float notePresident;

    private Float noteRapporteur;

    private Float noteExaminateur;

}