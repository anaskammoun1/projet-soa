package com.soutenance.features.encadrant.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "encadrants")
public class Encadrant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    private String grade;
    
    private String specialite;

    public Encadrant() {}

    public Encadrant(Long id, String nom, String prenom, String email, String grade, String specialite) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.grade = grade;
        this.specialite = specialite;
    }


    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
}