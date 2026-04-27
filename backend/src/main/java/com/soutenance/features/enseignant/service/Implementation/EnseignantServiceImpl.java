package com.soutenance.features.enseignant.service.Implementation;

import com.soutenance.features.enseignant.dto.EnseignantDTO;
import com.soutenance.features.enseignant.entity.Enseignant;
import com.soutenance.features.enseignant.repository.EnseignantRepository;
import com.soutenance.features.enseignant.service.Interface.EnseignantService;
import com.soutenance.exception.BusinessException;
import com.soutenance.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnseignantServiceImpl implements EnseignantService {

    private final EnseignantRepository repository;

    public EnseignantServiceImpl(EnseignantRepository repository) {
        this.repository = repository;
    }

    private EnseignantDTO toDTO(Enseignant e) {
        return new EnseignantDTO(
                e.getId(),
                e.getNom(),
                e.getPrenom(),
                e.getEmail(),
                e.getGrade(),
                e.getSpecialite());
    }

    private Enseignant toEntity(EnseignantDTO dto) {
        Enseignant e = new Enseignant();
        e.setId(dto.getId());
        e.setNom(dto.getNom());
        e.setPrenom(dto.getPrenom());
        e.setEmail(dto.getEmail());
        e.setGrade(dto.getGrade());
        e.setSpecialite(dto.getSpecialite());
        return e;
    }

    @Override
    public EnseignantDTO create(EnseignantDTO dto) {
        if (dto.getEmail() != null && repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email déjà utilisé");
        }

        Enseignant saved = repository.save(toEntity(dto));
        return toDTO(saved);
    }

    @Override
    public List<EnseignantDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EnseignantDTO getById(Long id) {
        return toDTO(getOrThrow(id));
    }

    @Override
    public EnseignantDTO update(Long id, EnseignantDTO dto) {
        Enseignant existing = getOrThrow(id);

        if (dto.getEmail() != null
                && !dto.getEmail().equals(existing.getEmail())
                && repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email déjà utilisé");
        }

        existing.setNom(dto.getNom());
        existing.setPrenom(dto.getPrenom());
        existing.setEmail(dto.getEmail());
        existing.setGrade(dto.getGrade());
        existing.setSpecialite(dto.getSpecialite());

        return toDTO(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Enseignant getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant non trouvé avec id = " + id));
    }
}