package com.soutenance.features.salle.service.Interface;

import com.soutenance.features.salle.dto.SalleDTO;
import com.soutenance.features.salle.entity.Salle;

import java.util.List;

public interface SalleService {
    SalleDTO create(SalleDTO dto);
    List<SalleDTO> getAll();
    SalleDTO getById(Long id);
    SalleDTO update(Long id, SalleDTO dto);
    void delete(Long id);
    Salle getOrThrow(Long id);
}