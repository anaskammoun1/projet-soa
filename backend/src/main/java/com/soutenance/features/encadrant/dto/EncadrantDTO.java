package com.soutenance.features.encadrant.dto;

public class EncadrantDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String grade;
    private String specialite;

    public EncadrantDTO() {}

    public EncadrantDTO(Long id, String nom, String prenom, String email, String grade, String specialite) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.grade = grade;
        this.specialite = specialite;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
}