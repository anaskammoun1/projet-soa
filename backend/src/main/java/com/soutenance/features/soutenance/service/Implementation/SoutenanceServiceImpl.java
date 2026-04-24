package com.soutenance.features.soutenance.service.Implementation;

import com.soutenance.features.soutenance.dto.SoutenanceDTO;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.repository.SoutenanceRepository;
import com.soutenance.features.enseignant.repository.EnseignantRepository;
import com.soutenance.features.salle.repository.SalleRepository;
import com.soutenance.features.etudiant.repository.EtudiantRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SoutenanceServiceImpl {

    private final SoutenanceRepository repository;
    private final EnseignantRepository enseignantRepository;
    private final SalleRepository salleRepository;
    private final EtudiantRepository etudiantRepository;

    public SoutenanceServiceImpl(
            SoutenanceRepository repository,
            EnseignantRepository enseignantRepository,
            SalleRepository salleRepository,
            EtudiantRepository etudiantRepository) {

        this.repository = repository;
        this.enseignantRepository = enseignantRepository;
        this.salleRepository = salleRepository;
        this.etudiantRepository = etudiantRepository;
    }

    private SoutenanceDTO toDTO(Soutenance s) {

        SoutenanceDTO dto = new SoutenanceDTO();

        dto.setId(s.getId());
        dto.setTitre(s.getTitre());
        dto.setDate(s.getDate());
        dto.setDuree(s.getDuree());
        dto.setStatut(s.getStatut());

        dto.setPresidentId(s.getPresident().getId());
        dto.setRapporteurId(s.getRapporteur().getId());
        dto.setExaminateurId(s.getExaminateur().getId());
        dto.setSalleId(s.getSalle().getId());
        dto.setEtudiantId(s.getEtudiant().getId());

        dto.setNotePresident(s.getNotePresident());
        dto.setNoteRapporteur(s.getNoteRapporteur());
        dto.setNoteExaminateur(s.getNoteExaminateur());

        return dto;
    }

    private Soutenance toEntity(SoutenanceDTO dto) {

        Soutenance s = new Soutenance();

        s.setId(dto.getId());
        s.setTitre(dto.getTitre());
        s.setDate(dto.getDate());
        s.setDuree(dto.getDuree());
        s.setStatut(dto.getStatut());

        s.setPresident(
                enseignantRepository.findById(dto.getPresidentId()).orElseThrow());

        s.setRapporteur(
                enseignantRepository.findById(dto.getRapporteurId()).orElseThrow());

        s.setExaminateur(
                enseignantRepository.findById(dto.getExaminateurId()).orElseThrow());

        s.setSalle(
                salleRepository.findById(dto.getSalleId()).orElseThrow());

        s.setEtudiant(
                etudiantRepository.findById(dto.getEtudiantId()).orElseThrow());

        s.setNotePresident(dto.getNotePresident());
        s.setNoteRapporteur(dto.getNoteRapporteur());
        s.setNoteExaminateur(dto.getNoteExaminateur());

        return s;
    }

    public SoutenanceDTO create(SoutenanceDTO dto) {

        Soutenance saved = repository.save(toEntity(dto));
        return toDTO(saved);
    }

    public List<SoutenanceDTO> getAll() {

        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SoutenanceDTO getById(Long id) {

        Soutenance s = repository.findById(id)
                .orElseThrow();

        return toDTO(s);
    }

    public SoutenanceDTO update(Long id, SoutenanceDTO dto) {

        Soutenance existing = repository.findById(id)
                .orElseThrow();

        Soutenance updated = toEntity(dto);
        updated.setId(existing.getId());

        return toDTO(repository.save(updated));
    }

    public void delete(Long id) {

        repository.deleteById(id);
    }

    public boolean existsConflitSalle(Long salleId, LocalDate date) {
        return repository.existsBySalleIdAndDate(salleId, date);
    }

    public Soutenance save(Soutenance s) {
        return repository.save(s);
    }
}