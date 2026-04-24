package com.soutenance.features.enseignant.service.Implementation;

import com.soutenance.features.enseignant.dto.EnseignantDTO;
import com.soutenance.features.enseignant.entity.Enseignant;
import com.soutenance.features.enseignant.repository.EnseignantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnseignantServiceImpl {

    private final EnseignantRepository repository;

    public EnseignantServiceImpl(EnseignantRepository repository) {
        this.repository = repository;
    }

    private EnseignantDTO toDTO(Enseignant e) {
        return new EnseignantDTO(e.getId(),e.getNom(),e.getPrenom(),e.getStudent_id(),e.getNote());
    }

    private Enseignant toEntity(EnseignantDTO dto) {
        Enseignant e = new Enseignant();
        e.setId(dto.getId());
        e.setNom(dto.getNom());
        e.setPrenom(dto.getPrenom());
        e.setStudent_id(dto.getStudent_id());
        e.setNote(dto.getNote());
        return e;
    }

    public EnseignantDTO create(EnseignantDTO dto) {
        Enseignant saved = repository.save(toEntity(dto));
        return toDTO(saved);
    }

    public List<EnseignantDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EnseignantDTO getById(Long id) {
        Enseignant e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enseignant not found"));
        return toDTO(e);
    }

    public EnseignantDTO update(Long id, EnseignantDTO dto) {
        Enseignant existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enseignant not found"));

        existing.setNom(dto.getNom());
        existing.setPrenom(dto.getPrenom());
        existing.setStudent_id(dto.getStudent_id());
        existing.setNote(dto.getNote());

        return toDTO(repository.save(existing));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}