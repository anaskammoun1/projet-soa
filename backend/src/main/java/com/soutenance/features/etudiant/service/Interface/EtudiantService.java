package com.soutenance.features.etudiant.service.Interface;

import com.soutenance.features.etudiant.dto.EtudiantDTO;
import com.soutenance.features.etudiant.entity.Etudiant;

import java.util.List;

public interface EtudiantService {

    EtudiantDTO createEtudiant(EtudiantDTO dto);

    EtudiantDTO getEtudiantById(Integer id);

    List<EtudiantDTO> getAllEtudiants();

    EtudiantDTO updateEtudiant(Integer id, EtudiantDTO dto);

    void deleteEtudiant(Integer id);

    boolean existsEtudiant(Integer id);

    Etudiant getOrThrow(Integer id);
}