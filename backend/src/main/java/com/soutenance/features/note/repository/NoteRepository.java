package com.soutenance.features.note.repository;

import com.soutenance.features.note.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findBySoutenanceId(Long soutenanceId);
    Optional<Note> findBySoutenanceIdAndEvaluateurId(Long soutenanceId, Long evaluateurId);
    boolean existsBySoutenanceIdAndEvaluateurId(Long soutenanceId, Long evaluateurId);
}


