package com.soutenance.security.user;

import com.soutenance.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

    Optional<ApplicationUser> findByUsername(String username);

    Optional<ApplicationUser> findByEtudiantId(Integer etudiantId);

    Optional<ApplicationUser> findByEnseignantId(Long enseignantId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);
}
