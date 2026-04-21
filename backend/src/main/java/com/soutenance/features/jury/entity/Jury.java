package com.soutenance.features.jury.entity;

import com.soutenance.features.encadrant.entity.Encadrant;
import com.soutenance.features.soutenance.entity.Soutenance;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "jurys")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Jury {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "soutenance_id", nullable = false, unique = true)
    private Soutenance soutenance;

    @ManyToOne
    @JoinColumn(name = "president_id", nullable = false)
    private Encadrant president;

    @ManyToOne
    @JoinColumn(name = "rapporteur_id", nullable = false)
    private Encadrant rapporteur;

    @ManyToOne
    @JoinColumn(name = "examinateur_id", nullable = false)
    private Encadrant examinateur;
}


