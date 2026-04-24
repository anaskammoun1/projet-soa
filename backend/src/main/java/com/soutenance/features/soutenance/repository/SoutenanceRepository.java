package com.soutenance.features.soutenance.repository;

import com.soutenance.features.soutenance.entity.Soutenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SoutenanceRepository extends JpaRepository<Soutenance, Long> {

    boolean existsBySalleIdAndDate(Long salleId, LocalDate date);
}