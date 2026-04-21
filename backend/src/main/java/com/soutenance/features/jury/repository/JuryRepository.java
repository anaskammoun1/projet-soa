package com.soutenance.features.jury.repository;

import com.soutenance.features.jury.entity.Jury;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JuryRepository extends JpaRepository<Jury, Long> {
    Optional<Jury> findBySoutenanceId(Long soutenanceId);
    boolean existsBySoutenanceId(Long soutenanceId);
}


