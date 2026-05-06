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

    private String presidentNom;

    private String rapporteurNom;

    private String examinateurNom;

    private String salleNom;

    private String etudiantNom;

    private String etudiantPrenom;

    public SoutenanceDTO(Long id,
                         String titre,
                         LocalDateTime date,
                         int duree,
                         StatutSoutenance statut,
                         Long presidentId,
                         Long rapporteurId,
                         Long examinateurId,
                         Long salleId,
                         Integer etudiantId,
                         Float notePresident,
                         Float noteRapporteur,
                         Float noteExaminateur) {
        this.id = id;
        this.titre = titre;
        this.date = date;
        this.duree = duree;
        this.statut = statut;
        this.presidentId = presidentId;
        this.rapporteurId = rapporteurId;
        this.examinateurId = examinateurId;
        this.salleId = salleId;
        this.etudiantId = etudiantId;
        this.notePresident = notePresident;
        this.noteRapporteur = noteRapporteur;
        this.noteExaminateur = noteExaminateur;
    }
}
