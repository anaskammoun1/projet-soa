package com.soutenance.features.enseignant.repository;

import com.soutenance.features.enseignant.entity.Enseignant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {

	boolean existsByEmail(String email);

	Optional<Enseignant> findByEmail(String email);

	@Query(nativeQuery = true, value = """
		SELECT DISTINCT e.* FROM enseignants e
		WHERE e.id NOT IN (
			SELECT COALESCE(encadrant_id, 0) FROM etudiants WHERE id = :etudiantId
		)
		ORDER BY e.nom, e.prenom
		""")
	List<Enseignant> findEnseignantsExcludingEncadrantOf(@Param("etudiantId") Integer etudiantId);

}