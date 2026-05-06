package com.soutenance.features.chatbot.service;

import com.soutenance.features.chatbot.model.ChatEvidence;
import com.soutenance.features.enseignant.entity.Enseignant;
import com.soutenance.features.enseignant.repository.EnseignantRepository;
import com.soutenance.features.etudiant.entity.Etudiant;
import com.soutenance.features.etudiant.repository.EtudiantRepository;
import com.soutenance.features.resultat.entity.Resultat;
import com.soutenance.features.resultat.repository.ResultatRepository;
import com.soutenance.features.salle.entity.Salle;
import com.soutenance.features.salle.repository.SalleRepository;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.repository.SoutenanceRepository;
import com.soutenance.security.user.ApplicationUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotKnowledgeService {

    private final SoutenanceRepository soutenanceRepository;
    private final ResultatRepository resultatRepository;
    private final EnseignantRepository enseignantRepository;
    private final EtudiantRepository etudiantRepository;
    private final SalleRepository salleRepository;

    public List<ChatEvidence> findEvidence(String question, ApplicationUser user) {
        String q = normalize(question);
        List<ChatEvidence> evidence = new ArrayList<>();

        if (mentions(q, "soutenance", "planif", "planification", "planning", "date", "statut")) {
            addSoutenancesEvidence(user, evidence);
        }
        if (mentions(q, "note", "notation", "eval", "evaluation")) {
            addNotesEvidence(user, evidence);
        }
        if (mentions(q, "resultat", "moyenne", "decision", "mention", "publie", "publication")) {
            addResultatsEvidence(user, evidence);
        }
        if (mentions(q, "salle", "disponibilite", "conflit", "disponible")) {
            addSallesEvidence(user, evidence);
        }
        if (mentions(q, "enseignant", "president", "rapporteur", "examinateur", "encadrant", "jury")) {
            addEnseignantEvidence(user, evidence);
        }
        if (mentions(q, "etudiant", "etudiants", "matricule", "nom", "prenom")) {
            addEtudiantEvidence(user, evidence);
        }

        return evidence;
    }

    private void addSoutenancesEvidence(ApplicationUser user, List<ChatEvidence> evidence) {
        if (user == null || user.getRole() == null) {
            return;
        }

        String role = user.getRole().name();
        if ("ADMIN".equals(role)) {
            soutenanceRepository.findAll().forEach(s -> evidence.add(new ChatEvidence("soutenance#" + s.getId(), formatSoutenance(s))));
            return;
        }

        if ("ETUDIANT".equals(role) && user.getEtudiantId() != null) {
            soutenanceRepository.findAllByEtudiant_Id(user.getEtudiantId())
                    .forEach(s -> evidence.add(new ChatEvidence("soutenance#" + s.getId(), formatSoutenance(s))));
            return;
        }

        if ("ENSEIGNANT".equals(role) && user.getEnseignantId() != null) {
            soutenanceRepository.findAllAssignedToTeacher(user.getEnseignantId())
                    .forEach(s -> evidence.add(new ChatEvidence("soutenance#" + s.getId(), formatSoutenance(s))));
        }
    }

    private void addNotesEvidence(ApplicationUser user, List<ChatEvidence> evidence) {
        addSoutenancesEvidence(user, evidence);
    }

    private void addResultatsEvidence(ApplicationUser user, List<ChatEvidence> evidence) {
        if (user == null || user.getRole() == null) {
            return;
        }

        String role = user.getRole().name();
        if ("ADMIN".equals(role)) {
            resultatRepository.findAll().forEach(r -> evidence.add(new ChatEvidence("resultat#" + r.getId(), formatResultat(r))));
            return;
        }

        if ("ETUDIANT".equals(role) && user.getEtudiantId() != null) {
            resultatRepository.findByEtudiantId(user.getEtudiantId().longValue())
                    .ifPresent(r -> evidence.add(new ChatEvidence("resultat#" + r.getId(), formatResultat(r))));
            return;
        }

        if ("ENSEIGNANT".equals(role) && user.getEnseignantId() != null) {
            resultatRepository.findAllAssignedToTeacher(user.getEnseignantId())
                    .forEach(r -> evidence.add(new ChatEvidence("resultat#" + r.getId(), formatResultat(r))));
        }
    }

    private void addSallesEvidence(ApplicationUser user, List<ChatEvidence> evidence) {
        if (user == null || user.getRole() == null) {
            return;
        }

        String role = user.getRole().name();
        if ("ADMIN".equals(role)) {
            salleRepository.findAll().forEach(s -> evidence.add(new ChatEvidence("salle#" + s.getId(), formatSalle(s))));
            return;
        }

        if ("ETUDIANT".equals(role) && user.getEtudiantId() != null) {
            soutenanceRepository.findAllByEtudiant_Id(user.getEtudiantId())
                    .forEach(s -> addSalleEvidenceFromSoutenance(s, evidence));
            return;
        }

        if ("ENSEIGNANT".equals(role) && user.getEnseignantId() != null) {
            soutenanceRepository.findAllAssignedToTeacher(user.getEnseignantId())
                    .forEach(s -> addSalleEvidenceFromSoutenance(s, evidence));
        }
    }

    private void addSalleEvidenceFromSoutenance(Soutenance soutenance, List<ChatEvidence> evidence) {
        if (soutenance.getSalle() != null) {
            evidence.add(new ChatEvidence("salle#" + soutenance.getSalle().getId(), formatSalle(soutenance.getSalle(), soutenance)));
        }
    }

    private void addEnseignantEvidence(ApplicationUser user, List<ChatEvidence> evidence) {
        if (user == null || user.getRole() == null) {
            return;
        }

        String role = user.getRole().name();
        if ("ADMIN".equals(role)) {
            enseignantRepository.findAll().forEach(e -> evidence.add(new ChatEvidence("enseignant#" + e.getId(), formatEnseignant(e))));
            return;
        }

        if (user.getEnseignantId() != null) {
            enseignantRepository.findById(user.getEnseignantId())
                    .ifPresent(e -> evidence.add(new ChatEvidence("enseignant#" + e.getId(), formatEnseignant(e))));
        }
    }

    private void addEtudiantEvidence(ApplicationUser user, List<ChatEvidence> evidence) {
        if (user == null || user.getRole() == null) {
            return;
        }

        String role = user.getRole().name();
        if ("ADMIN".equals(role)) {
            etudiantRepository.findAll().forEach(e -> evidence.add(new ChatEvidence("etudiant#" + e.getId(), formatEtudiant(e))));
            return;
        }

        if (user.getEtudiantId() != null) {
            etudiantRepository.findById(user.getEtudiantId())
                    .ifPresent(e -> evidence.add(new ChatEvidence("etudiant#" + e.getId(), formatEtudiant(e))));
        }
    }

    private String formatSoutenance(Soutenance s) {
        StringBuilder sb = new StringBuilder();
        sb.append("Titre: ").append(s.getTitre()).append("; ");
        sb.append("Date: ").append(s.getDate()).append("; ");
        sb.append("Statut: ").append(s.getStatut()).append("; ");
        if (s.getEtudiant() != null) sb.append("Etudiant: ").append(s.getEtudiant().getPrenom()).append(" ").append(s.getEtudiant().getNom()).append("; ");
        if (s.getSalle() != null) sb.append("Salle: ").append(s.getSalle().getNom()).append("; ");
        if (s.getPresident() != null) sb.append("President: ").append(s.getPresident().getPrenom()).append(" ").append(s.getPresident().getNom()).append("; ");
        if (s.getRapporteur() != null) sb.append("Rapporteur: ").append(s.getRapporteur().getPrenom()).append(" ").append(s.getRapporteur().getNom()).append("; ");
        if (s.getExaminateur() != null) sb.append("Examinateur: ").append(s.getExaminateur().getPrenom()).append(" ").append(s.getExaminateur().getNom()).append("; ");
        if (s.getNotePresident() != null) sb.append("NotePresident: ").append(s.getNotePresident()).append("; ");
        if (s.getNoteRapporteur() != null) sb.append("NoteRapporteur: ").append(s.getNoteRapporteur()).append("; ");
        if (s.getNoteExaminateur() != null) sb.append("NoteExaminateur: ").append(s.getNoteExaminateur()).append("; ");
        return sb.toString();
    }

    private String formatResultat(Resultat r) {
        return "SoutenanceId: " + r.getSoutenanceId() + "; MoyenneFinale: " + r.getMoyenneFinale() + "; Mention: " + r.getMention() + "; Decision: " + r.getDecisionFinale() + "; Publie: " + r.getPublie();
    }

    private String formatEnseignant(Enseignant e) {
        return "Nom: " + e.getPrenom() + " " + e.getNom() + "; Email: " + e.getEmail();
    }

    private String formatEtudiant(Etudiant e) {
        return "Nom: " + e.getPrenom() + " " + e.getNom() + "; Matricule: " + e.getMatricule() + "; Filiere: " + e.getFiliere() + "; Niveau: " + e.getNiveau();
    }

    private String formatSalle(Salle salle) {
        return "Nom: " + salle.getNom() + "; Capacite: " + salle.getCapacite() + "; Disponible: " + salle.isDisponible();
    }

    private String formatSalle(Salle salle, Soutenance soutenance) {
        return formatSalle(salle) + "; SoutenanceId: " + soutenance.getId() + "; Date: " + soutenance.getDate();
    }

    private boolean mentions(String value, String... terms) {
        for (String term : terms) {
            if (value.contains(normalize(term))) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return java.text.Normalizer.normalize(value.toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}