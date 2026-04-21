package com.soutenance.features.encadrant.service.Interface;

import com.soutenance.features.encadrant.dto.EncadrantDTO;
import com.soutenance.features.encadrant.entity.Encadrant;

import java.util.List;

public interface EncadrantService {

    EncadrantDTO createEncadrant(EncadrantDTO dto);

    EncadrantDTO getEncadrantById(Long id);

    List<EncadrantDTO> getAllEncadrants();

    EncadrantDTO updateEncadrant(Long id, EncadrantDTO dto);

    void deleteEncadrant(Long id);

    boolean existsEncadrant(Long id);

    Encadrant getOrThrow(Long id);
}