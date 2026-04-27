package com.soutenance.security.bootstrap;

import com.soutenance.features.enseignant.entity.Enseignant;
import com.soutenance.features.enseignant.repository.EnseignantRepository;
import com.soutenance.features.etudiant.entity.Etudiant;
import com.soutenance.features.etudiant.repository.EtudiantRepository;
import com.soutenance.features.note.dto.NoteDTO;
import com.soutenance.features.salle.entity.Salle;
import com.soutenance.features.salle.repository.SalleRepository;
import com.soutenance.features.soutenance.dto.SoutenanceDTO;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.repository.SoutenanceRepository;
import com.soutenance.orchestrator.NotationOrchestrator;
import com.soutenance.orchestrator.PlanificationOrchestrator;
import com.soutenance.security.Role;
import com.soutenance.security.user.ApplicationUser;
import com.soutenance.security.user.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Component
@Order(2)
@RequiredArgsConstructor
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final EnseignantRepository enseignantRepository;
    private final EtudiantRepository etudiantRepository;
    private final SalleRepository salleRepository;
    private final SoutenanceRepository soutenanceRepository;
    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlanificationOrchestrator planificationOrchestrator;
    private final NotationOrchestrator notationOrchestrator;

    @Value("${app.bootstrap.demo-enabled:false}")
    private boolean demoEnabled;

    @Value("${app.bootstrap.demo-password:}")
    private String demoPassword;

    @Value("${app.bootstrap.demo-seed-notes:true}")
    private boolean seedNotes;

    @Override
    public void run(ApplicationArguments args) {
        if (!demoEnabled) {
            return;
        }

        LOGGER.info("Seeding demo soutenance data.");

        Enseignant ayari = enseignant("Ayari", "Nadia", "nadia.ayari@example.local", "Professeur", "Genie logiciel");
        Enseignant benSalah = enseignant("Ben Salah", "Karim", "karim.bensalah@example.local", "Maitre de conferences", "Systemes distribues");
        Enseignant trabelsi = enseignant("Trabelsi", "Meriem", "meriem.trabelsi@example.local", "Docteur", "IA et data science");
        Enseignant mansouri = enseignant("Mansouri", "Sami", "sami.mansouri@example.local", "Professeur", "Reseaux et securite");

        Etudiant omar = etudiant("Safi", "Omar", "omar.safi@example.local", "GL2026001", "Genie logiciel", "Master 2");
        Etudiant lina = etudiant("Kacem", "Lina", "lina.kacem@example.local", "GL2026002", "IA et data science", "Master 2");
        Etudiant anis = etudiant("Mejri", "Anis", "anis.mejri@example.local", "GL2026003", "Reseaux et securite", "Master 2");

        Salle a101 = salle("A101", 30, "Bloc A - 1er etage");
        Salle b202 = salle("B202", 45, "Bloc B - 2eme etage");
        Salle c303 = salle("C303", 25, "Bloc C - 3eme etage");

        createDemoUsers(ayari, benSalah, trabelsi, omar, lina, anis);

        Soutenance soa = soutenance(
                "Architecture SOA pour la gestion des soutenances",
                LocalDateTime.now().plusDays(7).withHour(9).withMinute(0).withSecond(0).withNano(0),
                60,
                ayari,
                benSalah,
                trabelsi,
                a101,
                omar);

        Soutenance analytics = soutenance(
                "Tableau de bord analytique des resultats academiques",
                LocalDateTime.now().plusDays(8).withHour(10).withMinute(30).withSecond(0).withNano(0),
                60,
                benSalah,
                trabelsi,
                mansouri,
                b202,
                lina);

        Soutenance security = soutenance(
                "Controle d'acces securise pour plateformes universitaires",
                LocalDateTime.now().plusDays(9).withHour(14).withMinute(0).withSecond(0).withNano(0),
                60,
                mansouri,
                ayari,
                benSalah,
                c303,
                anis);

        if (seedNotes) {
            notesIfNew(soa, ayari, benSalah, trabelsi, 16.0, 15.0, 17.0);
            notesIfNew(analytics, benSalah, trabelsi, mansouri, 14.0, 15.0, 15.5);
            notesIfNew(security, mansouri, ayari, benSalah, 13.5, 14.0, 14.5);
        }

        LOGGER.info("Demo data seeding completed.");
    }

    private Enseignant enseignant(String nom, String prenom, String email, String grade, String specialite) {
        return enseignantRepository.findByEmail(email).orElseGet(() -> {
            Enseignant enseignant = new Enseignant();
            enseignant.setNom(nom);
            enseignant.setPrenom(prenom);
            enseignant.setEmail(email);
            enseignant.setGrade(grade);
            enseignant.setSpecialite(specialite);
            return enseignantRepository.save(enseignant);
        });
    }

    private Etudiant etudiant(String nom, String prenom, String email, String matricule, String filiere, String niveau) {
        return etudiantRepository.findByEmail(email).orElseGet(() -> etudiantRepository.save(
                new Etudiant(null, nom, prenom, email, matricule, filiere, niveau)));
    }

    private Salle salle(String nom, int capacite, String localisation) {
        return salleRepository.findByNom(nom).orElseGet(() -> {
            Salle salle = new Salle();
            salle.setNom(nom);
            salle.setCapacite(capacite);
            salle.setLocalisation(localisation);
            salle.setDisponible(true);
            return salleRepository.save(salle);
        });
    }

    private Soutenance soutenance(
            String titre,
            LocalDateTime date,
            int duree,
            Enseignant president,
            Enseignant rapporteur,
            Enseignant examinateur,
            Salle salle,
            Etudiant etudiant) {

        return soutenanceRepository.findByTitre(titre).orElseGet(() -> {
            SoutenanceDTO dto = new SoutenanceDTO();
            dto.setTitre(titre);
            dto.setDate(date);
            dto.setDuree(duree);
            dto.setPresidentId(president.getId());
            dto.setRapporteurId(rapporteur.getId());
            dto.setExaminateurId(examinateur.getId());
            dto.setSalleId(salle.getId());
            dto.setEtudiantId(etudiant.getId());
            return planificationOrchestrator.planifierSoutenance(dto);
        });
    }

    private void notesIfNew(
            Soutenance soutenance,
            Enseignant president,
            Enseignant rapporteur,
            Enseignant examinateur,
            double notePresident,
            double noteRapporteur,
            double noteExaminateur) {

        if (soutenance.getNotePresident() != null
                && soutenance.getNoteRapporteur() != null
                && soutenance.getNoteExaminateur() != null) {
            return;
        }

        note(soutenance.getId(), president.getId(), "PRESIDENT", notePresident);
        note(soutenance.getId(), rapporteur.getId(), "RAPPORTEUR", noteRapporteur);
        note(soutenance.getId(), examinateur.getId(), "EXAMINATEUR", noteExaminateur);
    }

    private void note(Long soutenanceId, Long evaluateurId, String roleJury, double value) {
        notationOrchestrator.saisirNote(new NoteDTO(
                null,
                soutenanceId,
                evaluateurId,
                roleJury,
                value,
                value,
                value,
                null));
    }

    private void createDemoUsers(
            Enseignant ayari,
            Enseignant benSalah,
            Enseignant trabelsi,
            Etudiant omar,
            Etudiant lina,
            Etudiant anis) {

        if (!StringUtils.hasText(demoPassword)) {
            LOGGER.warn("DEMO_DATA_ENABLED is true but DEMO_USER_PASSWORD is empty; domain data will be seeded without demo users.");
            return;
        }

        user("teacher.ayari", "teacher.ayari@example.local", Role.ENSEIGNANT, ayari.getId(), null);
        user("teacher.bensalah", "teacher.bensalah@example.local", Role.ENSEIGNANT, benSalah.getId(), null);
        user("teacher.trabelsi", "teacher.trabelsi@example.local", Role.ENSEIGNANT, trabelsi.getId(), null);
        user("student.omar", "student.omar@example.local", Role.ETUDIANT, null, omar.getId());
        user("student.lina", "student.lina@example.local", Role.ETUDIANT, null, lina.getId());
        user("student.anis", "student.anis@example.local", Role.ETUDIANT, null, anis.getId());
    }

    private void user(String username, String email, Role role, Long enseignantId, Integer etudiantId) {
        if (userRepository.existsByUsername(username)) {
            return;
        }

        userRepository.save(ApplicationUser.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(demoPassword))
                .role(role)
                .enseignantId(enseignantId)
                .etudiantId(etudiantId)
                .enabled(true)
                .build());
    }
}
