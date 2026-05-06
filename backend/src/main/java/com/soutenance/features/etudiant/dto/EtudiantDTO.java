package com.soutenance.features.etudiant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EtudiantDTO {

    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private String matricule;
    private String filiere;
    private String niveau;
    private Long encadrantId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public EtudiantDTO() {}

    public EtudiantDTO(Integer id, String nom, String prenom, String email,
                       String matricule, String filiere, String niveau) {
        this(id, nom, prenom, email, matricule, filiere, niveau, null);
    }

    public EtudiantDTO(Integer id, String nom, String prenom, String email,
                       String matricule, String filiere, String niveau, Long encadrantId) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.matricule = matricule;
        this.filiere = filiere;
        this.niveau = niveau;
        this.encadrantId = encadrantId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }

    public Long getEncadrantId() { return encadrantId; }
    public void setEncadrantId(Long encadrantId) { this.encadrantId = encadrantId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
