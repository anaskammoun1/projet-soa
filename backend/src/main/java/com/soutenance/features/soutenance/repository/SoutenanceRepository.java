package com.soutenance.features.soutenance.repository;

import com.soutenance.features.soutenance.entity.Soutenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SoutenanceRepository extends JpaRepository<Soutenance, Long> {

    List<Soutenance> findAllByEtudiant_Id(Integer etudiantId);

    @Query("""
        SELECT s
        FROM Soutenance s
        WHERE s.president.id = :enseignantId
           OR s.rapporteur.id = :enseignantId
           OR s.examinateur.id = :enseignantId
        """)
    List<Soutenance> findAllAssignedToTeacher(@Param("enseignantId") Long enseignantId);

    @Query("""
        SELECT COUNT(s)
        FROM Soutenance s
        WHERE s.etudiant.id = :etudiantId
          AND (s.president.id = :enseignantId
           OR s.rapporteur.id = :enseignantId
           OR s.examinateur.id = :enseignantId)
        """)
    long countAssignedToTeacherForEtudiant(@Param("enseignantId") Long enseignantId,
                                           @Param("etudiantId") Integer etudiantId);

    Optional<Soutenance> findByTitre(String titre);

    boolean existsByTitre(String titre);

    @Query("""
        SELECT COUNT(s)
        FROM Soutenance s
        WHERE s.etudiant.id = :etudiantId
          AND (:excludeId IS NULL OR s.id <> :excludeId)
        """)
    long countByEtudiantExcludingSoutenance(@Param("etudiantId") Integer etudiantId,
                                            @Param("excludeId") Long excludeId);

    @Query(value = """
        SELECT COUNT(*)
        FROM soutenances s
        WHERE s.salle_id = :salleId
          AND (:excludeId IS NULL OR s.id <> :excludeId)
          AND s.date < :fin
          AND DATE_ADD(s.date, INTERVAL s.duree MINUTE) > :debut
        """, nativeQuery = true)
    long countSalleConflicts(@Param("salleId") Long salleId,
                @Param("debut") LocalDateTime debut,
                @Param("fin") LocalDateTime fin,
                @Param("excludeId") Long excludeId);

    @Query(value = """
        SELECT COUNT(*)
        FROM soutenances s
        WHERE (:excludeId IS NULL OR s.id <> :excludeId)
          AND s.date < :fin
          AND DATE_ADD(s.date, INTERVAL s.duree MINUTE) > :debut
          AND (s.president_id = :enseignantId
           OR s.rapporteur_id = :enseignantId
           OR s.examinateur_id = :enseignantId)
        """, nativeQuery = true)
    long countEnseignantConflicts(@Param("enseignantId") Long enseignantId,
                     @Param("debut") LocalDateTime debut,
                     @Param("fin") LocalDateTime fin,
                     @Param("excludeId") Long excludeId);

    @Query(value = """
        SELECT COUNT(*)
        FROM soutenances s
        WHERE s.etudiant_id = :etudiantId
          AND (:excludeId IS NULL OR s.id <> :excludeId)
          AND s.date < :fin
          AND DATE_ADD(s.date, INTERVAL s.duree MINUTE) > :debut
        """, nativeQuery = true)
    long countEtudiantConflicts(@Param("etudiantId") Integer etudiantId,
                   @Param("debut") LocalDateTime debut,
                   @Param("fin") LocalDateTime fin,
                   @Param("excludeId") Long excludeId);
}
