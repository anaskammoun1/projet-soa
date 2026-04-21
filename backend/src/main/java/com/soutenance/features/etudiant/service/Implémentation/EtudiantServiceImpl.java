package com.soutenance.features.etudiant.service.Implémentation;

import com.soutenance.features.etudiant.dto.EtudiantDTO;
import com.soutenance.features.etudiant.entity.Etudiant;
import com.soutenance.features.etudiant.repository.EtudiantRepository;
import com.soutenance.features.etudiant.service.Interface.EtudiantService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EtudiantServiceImpl implements EtudiantService {

    private final EtudiantRepository repository;

    public EtudiantServiceImpl(EtudiantRepository repository) {
        this.repository = repository;
    }

    private EtudiantDTO mapToDTO(Etudiant e) {
        return new EtudiantDTO(
                e.getId(),
                e.getNom(),
                e.getPrenom(),
                e.getEmail(),
                e.getMatricule(),
                e.getFiliere(),
                e.getNiveau()
        );
    }

    private Etudiant mapToEntity(EtudiantDTO dto) {
        return new Etudiant(
                dto.getId(),
                dto.getNom(),
                dto.getPrenom(),
                dto.getEmail(),
                dto.getMatricule(),
                dto.getFiliere(),
                dto.getNiveau()
        );
    }

    @Override
    public EtudiantDTO createEtudiant(EtudiantDTO dto) {

        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email déjà utilisé !");
        }

        if (repository.existsByMatricule(dto.getMatricule())) {
            throw new RuntimeException("Matricule déjà utilisé !");
        }

        Etudiant saved = repository.save(mapToEntity(dto));
        return mapToDTO(saved);
    }

    @Override
    public EtudiantDTO getEtudiantById(Integer id) {
        return mapToDTO(getOrThrow(id));
    }

    @Override
    public List<EtudiantDTO> getAllEtudiants() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EtudiantDTO updateEtudiant(Integer id, EtudiantDTO dto) {

        Etudiant existing = getOrThrow(id);

        existing.setNom(dto.getNom());
        existing.setPrenom(dto.getPrenom());
        existing.setEmail(dto.getEmail());
        existing.setMatricule(dto.getMatricule());
        existing.setFiliere(dto.getFiliere());
        existing.setNiveau(dto.getNiveau());

        return mapToDTO(repository.save(existing));
    }

    @Override
    public void deleteEtudiant(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsEtudiant(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public Etudiant getOrThrow(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Etudiant non trouvé avec id = " + id));
    }
}