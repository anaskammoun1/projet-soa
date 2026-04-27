package com.soutenance.features.jury.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JuryDTO {

    private Long soutenanceId;
    private Long presidentId;
    private Long rapporteurId;
    private Long examinateurId;
}