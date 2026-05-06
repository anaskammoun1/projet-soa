package com.soutenance.features.enseignant.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnseignantDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String grade;
    private String specialite;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public EnseignantDTO(Long id, String nom, String prenom, String email, String grade, String specialite) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.grade = grade;
        this.specialite = specialite;
    }
}
