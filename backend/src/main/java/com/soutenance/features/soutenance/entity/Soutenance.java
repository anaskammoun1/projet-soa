package com.soutenance.features.soutenance.entity;

import com.soutenance.features.enseignant.entity.Enseignant;
import com.soutenance.features.salle.entity.Salle;
import com.soutenance.features.etudiant.entity.Etudiant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "soutenances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Soutenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    private LocalDateTime date;

    private int duree;

    @Enumerated(EnumType.STRING)
    private StatutSoutenance statut;

    @ManyToOne
    @JoinColumn(name = "president_id")
    private Enseignant president;

    @ManyToOne
    @JoinColumn(name = "rapporteur_id")
    private Enseignant rapporteur;

    @ManyToOne
    @JoinColumn(name = "examinateur_id")
    private Enseignant examinateur;

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    @OneToOne
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;

    private Float notePresident;

    private Float noteRapporteur;

    private Float noteExaminateur;

}