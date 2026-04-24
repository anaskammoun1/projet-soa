package com.soutenance.features.salle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalleDTO {

    private Long id;

    private String nom;

    private int capacite;

    private String localisation;

    private boolean disponible;
}