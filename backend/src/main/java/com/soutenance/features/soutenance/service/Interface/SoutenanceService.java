package com.soutenance.features.soutenance.service.Interface;

import com.soutenance.features.soutenance.dto.SoutenanceDTO;
import com.soutenance.features.soutenance.entity.Soutenance;

import java.time.LocalDateTime;
import java.util.List;

public interface SoutenanceService {
    SoutenanceDTO create(SoutenanceDTO dto);
    List<SoutenanceDTO> getAll();
    SoutenanceDTO getById(Long id);
    SoutenanceDTO update(Long id, SoutenanceDTO dto);
    void delete(Long id);
    boolean existsConflitSalle(Long salleId, LocalDateTime date, LocalDateTime fin);
    Soutenance save(Soutenance s);
}