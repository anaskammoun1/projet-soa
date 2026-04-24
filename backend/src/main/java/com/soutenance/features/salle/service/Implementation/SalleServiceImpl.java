package com.soutenance.features.salle.service.Implementation;

import com.soutenance.features.salle.dto.SalleDTO;
import com.soutenance.features.salle.entity.Salle;
import com.soutenance.features.salle.repository.SalleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalleServiceImpl {

    private final SalleRepository repository;

    public SalleServiceImpl(SalleRepository repository) {
        this.repository = repository;
    }

    private SalleDTO toDTO(Salle s) {

        return new SalleDTO(
                s.getId(),
                s.getNom(),
                s.getCapacite(),
                s.getLocalisation(),
                s.isDisponible()
        );
    }

    private Salle toEntity(SalleDTO dto) {

        Salle s = new Salle();

        s.setId(dto.getId());
        s.setNom(dto.getNom());
        s.setCapacite(dto.getCapacite());
        s.setLocalisation(dto.getLocalisation());
        s.setDisponible(dto.isDisponible());

        return s;
    }

    public SalleDTO create(SalleDTO dto) {

        return toDTO(repository.save(toEntity(dto)));
    }

    public List<SalleDTO> getAll() {

        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SalleDTO getById(Long id) {

        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow();
    }

    public SalleDTO update(Long id, SalleDTO dto) {

        Salle existing = repository.findById(id).orElseThrow();

        Salle updated = toEntity(dto);
        updated.setId(existing.getId());

        return toDTO(repository.save(updated));
    }

    public void delete(Long id) {

        repository.deleteById(id);
    }

    public Salle getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salle not found"));
    }
}