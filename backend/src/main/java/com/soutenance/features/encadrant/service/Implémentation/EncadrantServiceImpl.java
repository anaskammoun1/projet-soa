package com.soutenance.features.encadrant.service.Implémentation;

import com.soutenance.features.encadrant.dto.EncadrantDTO;
import com.soutenance.features.encadrant.entity.Encadrant;
import com.soutenance.features.encadrant.repository.EncadrantRepository;
import com.soutenance.features.encadrant.service.Interface.EncadrantService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EncadrantServiceImpl implements EncadrantService {

    private final EncadrantRepository repository;

    public EncadrantServiceImpl(EncadrantRepository repository) {
        this.repository = repository;
    }

    private EncadrantDTO mapToDTO(Encadrant e) {
        return new EncadrantDTO(
                e.getId(),
                e.getNom(),
                e.getPrenom(),
                e.getEmail(),
                e.getGrade(),
                e.getSpecialite()
        );
    }

    private Encadrant mapToEntity(EncadrantDTO dto) {
        return new Encadrant(
                dto.getId(),
                dto.getNom(),
                dto.getPrenom(),
                dto.getEmail(),
                dto.getGrade(),
                dto.getSpecialite()
        );
    }

    @Override
    public EncadrantDTO createEncadrant(EncadrantDTO dto) {

        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email déjà utilisé !");
        }

        Encadrant saved = repository.save(mapToEntity(dto));
        return mapToDTO(saved);
    }

    @Override
    public EncadrantDTO getEncadrantById(Long id) {
        return mapToDTO(getOrThrow(id));
    }

    @Override
    public List<EncadrantDTO> getAllEncadrants() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EncadrantDTO updateEncadrant(Long id, EncadrantDTO dto) {

        Encadrant existing = getOrThrow(id);

        existing.setNom(dto.getNom());
        existing.setPrenom(dto.getPrenom());
        existing.setEmail(dto.getEmail());
        existing.setGrade(dto.getGrade());
        existing.setSpecialite(dto.getSpecialite());

        return mapToDTO(repository.save(existing));
    }

    @Override
    public void deleteEncadrant(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsEncadrant(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Encadrant getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encadrant non trouvé"));
    }
}