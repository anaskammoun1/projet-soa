package com.soutenance.security;

import com.soutenance.security.user.ApplicationUser;
import com.soutenance.security.user.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final ApplicationUserRepository userRepository;

    public ApplicationUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("No authenticated user");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    public boolean isAdmin() {
        return getCurrentUser().getRole() == Role.ADMIN;
    }

    public boolean isTeacher() {
        return getCurrentUser().getRole() == Role.ENSEIGNANT;
    }

    public boolean isStudent() {
        return getCurrentUser().getRole() == Role.ETUDIANT;
    }

    public Long getCurrentEnseignantId() {
        return getCurrentUser().getEnseignantId();
    }

    public Integer getCurrentEtudiantId() {
        return getCurrentUser().getEtudiantId();
    }
}
