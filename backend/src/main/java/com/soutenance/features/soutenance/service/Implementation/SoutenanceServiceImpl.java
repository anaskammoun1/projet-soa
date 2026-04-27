package com.soutenance.features.soutenance.service.Implementation;

import com.soutenance.exception.ResourceNotFoundException;
import com.soutenance.features.soutenance.dto.SoutenanceDTO;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.entity.StatutSoutenance;
import com.soutenance.features.soutenance.repository.SoutenanceRepository;
import com.soutenance.features.soutenance.service.Interface.SoutenanceService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SoutenanceServiceImpl implements SoutenanceService {

    private final SoutenanceRepository repository;

    public SoutenanceServiceImpl(SoutenanceRepository repository) {
        this.repository = repository;
    }

    private SoutenanceDTO toDTO(Soutenance s) {
        SoutenanceDTO dto = new SoutenanceDTO();
        dto.setId(s.getId());
        dto.setTitre(s.getTitre());
        dto.setDate(s.getDate());
        dto.setDuree(s.getDuree());
        dto.setStatut(s.getStatut());
        dto.setPresidentId(s.getPresident() != null ? s.getPresident().getId() : null);
        dto.setRapporteurId(s.getRapporteur() != null ? s.getRapporteur().getId() : null);
        dto.setExaminateurId(s.getExaminateur() != null ? s.getExaminateur().getId() : null);
        dto.setSalleId(s.getSalle() != null ? s.getSalle().getId() : null);
        dto.setEtudiantId(s.getEtudiant() != null ? s.getEtudiant().getId() : null);
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
        s.setStatut(dto.getStatut() != null ? dto.getStatut() : StatutSoutenance.PLANIFIEE);
        s.setNotePresident(dto.getNotePresident());
        s.setNoteRapporteur(dto.getNoteRapporteur());
        s.setNoteExaminateur(dto.getNoteExaminateur());
        return s;
    }

    @Override
    public SoutenanceDTO create(SoutenanceDTO dto) {
        return toDTO(repository.save(toEntity(dto)));
    }

    @Override
    public List<SoutenanceDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SoutenanceDTO getById(Long id) {
        return toDTO(getOrThrow(id));
    }

    @Override
    public SoutenanceDTO update(Long id, SoutenanceDTO dto) {
        Soutenance existing = getOrThrow(id);
        if (dto.getTitre() != null) {
            existing.setTitre(dto.getTitre());
        }
        if (dto.getDate() != null) {
            existing.setDate(dto.getDate());
        }
        if (dto.getDuree() > 0) {
            existing.setDuree(dto.getDuree());
        }
        if (dto.getStatut() != null) {
            existing.setStatut(dto.getStatut());
        }
        if (dto.getNotePresident() != null) {
            existing.setNotePresident(dto.getNotePresident());
        }
        if (dto.getNoteRapporteur() != null) {
            existing.setNoteRapporteur(dto.getNoteRapporteur());
        }
        if (dto.getNoteExaminateur() != null) {
            existing.setNoteExaminateur(dto.getNoteExaminateur());
        }
        return toDTO(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<SoutenanceDTO> getByEtudiantId(Integer etudiantId) {
        return repository.findAllByEtudiant_Id(etudiantId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SoutenanceDTO updateStatut(Long id, StatutSoutenance statut) {
        Soutenance soutenance = getOrThrow(id);
        soutenance.setStatut(statut);
        return toDTO(repository.save(soutenance));
    }

    @Override
    public boolean existsConflitSalle(Long salleId, LocalDateTime date, LocalDateTime fin, Long excludeSoutenanceId) {
        return repository.existsSalleConflict(salleId, date, fin, excludeSoutenanceId);
    }

    @Override
    public boolean existsConflitEncadrant(Long enseignantId, LocalDateTime date, LocalDateTime fin, Long excludeSoutenanceId) {
        return repository.existsEnseignantConflict(enseignantId, date, fin, excludeSoutenanceId);
    }

    @Override
    public boolean existsConflitEtudiant(Integer etudiantId, LocalDateTime date, LocalDateTime fin, Long excludeSoutenanceId) {
        return repository.existsEtudiantConflict(etudiantId, date, fin, excludeSoutenanceId);
    }

    @Override
    public Soutenance getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvee avec id = " + id));
    }

    @Override
    public Soutenance save(Soutenance s) {
        return repository.save(s);
    }
}
