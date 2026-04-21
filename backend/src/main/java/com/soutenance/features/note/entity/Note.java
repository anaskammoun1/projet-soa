package com.soutenance.features.note.entity;

import com.soutenance.features.encadrant.entity.Encadrant;
import com.soutenance.features.soutenance.entity.Soutenance;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "soutenance_id", nullable = false)
    private Soutenance soutenance;

    @ManyToOne
    @JoinColumn(name = "evaluateur_id", nullable = false)
    private Encadrant evaluateur;

    @Enumerated(EnumType.STRING)
    private RoleJury roleJury;

    @DecimalMin(value = "0.0", message = "La note doit être >= 0")
    @DecimalMax(value = "20.0", message = "La note doit être <= 20")
    private Double noteExpose;       // note exposé oral

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "20.0")
    private Double noteRapport;      // note du rapport écrit

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "20.0")
    private Double noteQuestions;    // note Q&A

    private String commentaire;

    public enum RoleJury {
        PRESIDENT, RAPPORTEUR, EXAMINATEUR
    }

    /**
     * Calcule la moyenne des notes de cet évaluateur.
     * Pondération : Rapport 40%, Exposé 40%, Questions 20%
     */
    public double getMoyenneEvaluateur() {
        if (noteExpose == null || noteRapport == null || noteQuestions == null) {
            throw new IllegalStateException("Toutes les notes doivent être saisies");
        }
        return (noteRapport * 0.4) + (noteExpose * 0.4) + (noteQuestions * 0.2);
    }
}


