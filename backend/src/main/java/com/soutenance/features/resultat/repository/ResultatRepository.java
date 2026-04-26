package com.soutenance.features.resultat.repository;

import com.soutenance.features.resultat.entity.Resultat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResultatRepository extends JpaRepository<Resultat, Long> {

    Optional<Resultat> findBySoutenanceId(Long soutenanceId);

    Optional<Resultat> findByEtudiantId(Long etudiantId);

    List<Resultat> findByPublieTrue();

    List<Resultat> findByValideFalse();

    boolean existsBySoutenanceId(Long soutenanceId);

    @Query("SELECT COUNT(r) FROM Resultat r WHERE r.decisionFinale = 'ADMIS' AND r.publie = true")
    long countAdmis();

    @Query("SELECT COUNT(r) FROM Resultat r WHERE r.decisionFinale = 'AJOURNE' AND r.publie = true")
    long countAjourne();

    @Query("SELECT COUNT(r) FROM Resultat r WHERE r.publie = true")
    long countPublished();
}
