package com.soutenance.features.salle.repository;

import com.soutenance.features.salle.entity.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalleRepository extends JpaRepository<Salle, Long> {

}