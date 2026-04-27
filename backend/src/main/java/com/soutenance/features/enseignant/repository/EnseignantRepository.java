package com.soutenance.features.enseignant.repository;

import com.soutenance.features.enseignant.entity.Enseignant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {

	boolean existsByEmail(String email);

	Optional<Enseignant> findByEmail(String email);

}