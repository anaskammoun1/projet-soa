package com.soutenance.features.encadrant.repository;

import com.soutenance.features.encadrant.entity.Encadrant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncadrantRepository extends JpaRepository<Encadrant, Long> {

    boolean existsByEmail(String email);
}