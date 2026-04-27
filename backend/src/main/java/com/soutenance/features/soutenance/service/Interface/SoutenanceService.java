package com.soutenance.features.soutenance.service.Interface;

import com.soutenance.features.soutenance.dto.SoutenanceDTO;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.entity.StatutSoutenance;

import java.time.LocalDateTime;
import java.util.List;

public interface SoutenanceService {
    SoutenanceDTO create(SoutenanceDTO dto);
    List<SoutenanceDTO> getAll();
    SoutenanceDTO getById(Long id);
    SoutenanceDTO update(Long id, SoutenanceDTO dto);
    void delete(Long id);
    List<SoutenanceDTO> getByEtudiantId(Integer etudiantId);
    SoutenanceDTO updateStatut(Long id, StatutSoutenance statut);
    boolean existsConflitSalle(Long salleId, LocalDateTime date, LocalDateTime fin, Long excludeSoutenanceId);
    boolean existsConflitEncadrant(Long enseignantId, LocalDateTime date, LocalDateTime fin, Long excludeSoutenanceId);
    boolean existsConflitEtudiant(Integer etudiantId, LocalDateTime date, LocalDateTime fin, Long excludeSoutenanceId);
    Soutenance getOrThrow(Long id);
    Soutenance save(Soutenance s);
}
