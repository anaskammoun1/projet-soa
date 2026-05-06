package com.soutenance.features.etudiant.service.Implémentation;

import com.soutenance.features.etudiant.dto.EtudiantDTO;
import com.soutenance.features.etudiant.entity.Etudiant;
import com.soutenance.features.etudiant.repository.EtudiantRepository;
import com.soutenance.features.etudiant.service.Interface.EtudiantService;
import com.soutenance.features.enseignant.repository.EnseignantRepository;
import com.soutenance.security.Role;
import com.soutenance.security.user.ApplicationUser;
import com.soutenance.security.user.ApplicationUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EtudiantServiceImpl implements EtudiantService {

    private final EtudiantRepository repository;
    private final EnseignantRepository enseignantRepository;
    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EtudiantServiceImpl(
            EtudiantRepository repository,
            EnseignantRepository enseignantRepository,
            ApplicationUserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.enseignantRepository = enseignantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private EtudiantDTO mapToDTO(Etudiant e) {
        return new EtudiantDTO(
                e.getId(),
                e.getNom(),
                e.getPrenom(),
                e.getEmail(),
                e.getMatricule(),
                e.getFiliere(),
                e.getNiveau(),
                e.getEncadrantId()
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
                dto.getNiveau(),
                dto.getEncadrantId()
        );
    }

    @Override
    @Transactional
    public EtudiantDTO createEtudiant(EtudiantDTO dto) {
        validateEncadrant(dto.getEncadrantId());
        validatePassword(dto.getPassword(), true);

        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email déjà utilisé !");
        }

        if (userRepository.existsByUsername(dto.getEmail()) || userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Un compte utilisateur existe déjà avec cet email");
        }

        if (repository.existsByMatricule(dto.getMatricule())) {
            throw new RuntimeException("Matricule déjà utilisé !");
        }

        Etudiant saved = repository.save(mapToEntity(dto));
        createStudentUser(saved, dto.getPassword());
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
    @Transactional
    public EtudiantDTO updateEtudiant(Integer id, EtudiantDTO dto) {

        Etudiant existing = getOrThrow(id);
        validateEncadrant(dto.getEncadrantId());
        validatePassword(dto.getPassword(), false);

        if (dto.getEmail() != null
                && !dto.getEmail().equals(existing.getEmail())
                && repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email déjà utilisé !");
        }

        String previousEmail = existing.getEmail();
        existing.setNom(dto.getNom());
        existing.setPrenom(dto.getPrenom());
        existing.setEmail(dto.getEmail());
        existing.setMatricule(dto.getMatricule());
        existing.setFiliere(dto.getFiliere());
        existing.setNiveau(dto.getNiveau());
        existing.setEncadrantId(dto.getEncadrantId());

        Etudiant saved = repository.save(existing);
        syncStudentUser(saved, previousEmail, dto.getPassword());
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void deleteEtudiant(Integer id) {
        userRepository.findByEtudiantId(id).ifPresent(userRepository::delete);
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

    private void validateEncadrant(Long encadrantId) {
        if (encadrantId == null) {
            throw new RuntimeException("Encadrant obligatoire");
        }

        if (!enseignantRepository.existsById(encadrantId)) {
            throw new RuntimeException("Encadrant non trouvé avec id = " + encadrantId);
        }
    }

    private void createStudentUser(Etudiant etudiant, String rawPassword) {
        userRepository.save(ApplicationUser.builder()
                .username(etudiant.getEmail())
                .email(etudiant.getEmail())
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(Role.ETUDIANT)
                .etudiantId(etudiant.getId())
                .enabled(true)
                .build());
    }

    private void syncStudentUser(Etudiant etudiant, String previousEmail, String rawPassword) {
        userRepository.findByEtudiantId(etudiant.getId()).ifPresentOrElse(user -> {
            if (!etudiant.getEmail().equals(previousEmail)
                    && userRepository.existsByUsername(etudiant.getEmail())) {
                throw new RuntimeException("Un compte utilisateur existe déjà avec cet email");
            }
            user.setUsername(etudiant.getEmail());
            user.setEmail(etudiant.getEmail());
            if (hasText(rawPassword)) {
                user.setPasswordHash(passwordEncoder.encode(rawPassword));
            }
            userRepository.save(user);
        }, () -> {
            if (hasText(rawPassword)) {
                createStudentUser(etudiant, rawPassword);
            }
        });
    }

    private void validatePassword(String password, boolean required) {
        if (!hasText(password)) {
            if (required) {
                throw new RuntimeException("Mot de passe obligatoire");
            }
            return;
        }
        if (password.length() < 6) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
