package com.soutenance.features.enseignant.dto;
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
}
