package com.soutenance.features.etudiant.repository;

import com.soutenance.features.etudiant.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Integer> {

    boolean existsByEmail(String email);
    boolean existsByMatricule(String matricule);
    Optional<Etudiant> findByEmail(String email);

}

