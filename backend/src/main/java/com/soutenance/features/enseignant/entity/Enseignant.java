package com.soutenance.features.enseignant.entity;

import com.soutenance.features.etudiant.entity.Etudiant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "enseignants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enseignant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nom;
    @Column
    private String prenom;
    @Column
    private int student_id;
    @Column
    private float note;
    @OneToMany(mappedBy = "encadrant")
    private List<Etudiant> etudiants;
}


