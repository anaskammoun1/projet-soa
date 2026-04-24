package com.soutenance.features.enseignant.repository;

import com.soutenance.features.enseignant.entity.Enseignant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {

}