package com.soutenance.features.enseignant.service.Implementation;

import com.soutenance.features.enseignant.dto.EnseignantDTO;
import com.soutenance.features.enseignant.entity.Enseignant;
import com.soutenance.features.enseignant.repository.EnseignantRepository;
import com.soutenance.features.enseignant.service.Interface.EnseignantService;
import com.soutenance.exception.BusinessException;
import com.soutenance.exception.ResourceNotFoundException;
import com.soutenance.security.Role;
import com.soutenance.security.user.ApplicationUser;
import com.soutenance.security.user.ApplicationUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnseignantServiceImpl implements EnseignantService {

    private final EnseignantRepository repository;
    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EnseignantServiceImpl(
            EnseignantRepository repository,
            ApplicationUserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    @Transactional
    public EnseignantDTO create(EnseignantDTO dto) {
        validatePassword(dto.getPassword(), true);

        if (dto.getEmail() != null && repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email déjà utilisé");
        }

        if (userRepository.existsByUsername(dto.getEmail()) || userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Un compte utilisateur existe déjà avec cet email");
        }

        Enseignant saved = repository.save(toEntity(dto));
        createTeacherUser(saved, dto.getPassword());
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
    @Transactional
    public EnseignantDTO update(Long id, EnseignantDTO dto) {
        Enseignant existing = getOrThrow(id);
        validatePassword(dto.getPassword(), false);

        if (dto.getEmail() != null
                && !dto.getEmail().equals(existing.getEmail())
                && repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email déjà utilisé");
        }

        String previousEmail = existing.getEmail();
        existing.setNom(dto.getNom());
        existing.setPrenom(dto.getPrenom());
        existing.setEmail(dto.getEmail());
        existing.setGrade(dto.getGrade());
        existing.setSpecialite(dto.getSpecialite());

        Enseignant saved = repository.save(existing);
        syncTeacherUser(saved, previousEmail, dto.getPassword());
        return toDTO(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.findByEnseignantId(id).ifPresent(userRepository::delete);
        repository.deleteById(id);
    }

    @Override
    public Enseignant getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant non trouvé avec id = " + id));
    }

    private void createTeacherUser(Enseignant enseignant, String rawPassword) {
        userRepository.save(ApplicationUser.builder()
                .username(enseignant.getEmail())
                .email(enseignant.getEmail())
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(Role.ENSEIGNANT)
                .enseignantId(enseignant.getId())
                .enabled(true)
                .build());
    }

    private void syncTeacherUser(Enseignant enseignant, String previousEmail, String rawPassword) {
        userRepository.findByEnseignantId(enseignant.getId()).ifPresentOrElse(user -> {
            if (!enseignant.getEmail().equals(previousEmail)
                    && userRepository.existsByUsername(enseignant.getEmail())) {
                throw new BusinessException("Un compte utilisateur existe déjà avec cet email");
            }
            user.setUsername(enseignant.getEmail());
            user.setEmail(enseignant.getEmail());
            if (hasText(rawPassword)) {
                user.setPasswordHash(passwordEncoder.encode(rawPassword));
            }
            userRepository.save(user);
        }, () -> {
            if (hasText(rawPassword)) {
                createTeacherUser(enseignant, rawPassword);
            }
        });
    }

    private void validatePassword(String password, boolean required) {
        if (!hasText(password)) {
            if (required) {
                throw new BusinessException("Mot de passe obligatoire");
            }
            return;
        }
        if (password.length() < 6) {
            throw new BusinessException("Le mot de passe doit contenir au moins 6 caractères");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
