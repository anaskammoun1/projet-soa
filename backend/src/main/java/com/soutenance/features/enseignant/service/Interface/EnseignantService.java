package com.soutenance.features.enseignant.service.Interface;

import com.soutenance.features.enseignant.dto.EnseignantDTO;
import com.soutenance.features.enseignant.entity.Enseignant;

import java.util.List;

public interface EnseignantService {

    EnseignantDTO create(EnseignantDTO dto);

    List<EnseignantDTO> getAll();

    EnseignantDTO getById(Long id);

    EnseignantDTO update(Long id, EnseignantDTO dto);

    void delete(Long id);

    Enseignant getOrThrow(Long id);
}