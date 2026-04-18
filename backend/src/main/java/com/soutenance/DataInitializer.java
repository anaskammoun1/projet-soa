package com.soutenance;

import com.soutenance.features.encadrant.entity.Encadrant;
import com.soutenance.features.encadrant.repository.EncadrantRepository;
import com.soutenance.features.etudiant.entity.Etudiant;
import com.soutenance.features.etudiant.repository.EtudiantRepository;
import com.soutenance.features.salle.entity.Salle;
import com.soutenance.features.salle.repository.SalleRepository;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.repository.SoutenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EtudiantRepository etudiantRepo;
    private final EncadrantRepository encadrantRepo;
    private final SalleRepository salleRepo;
    private final SoutenanceRepository soutenanceRepo;

    @Override
    public void run(String... args) {

        // ── Étudiants ────────────────────────────────────────────────────
        Etudiant e1 = new Etudiant(null, "Benali",  "Amine",  "amine.benali@univ.dz",  "20210001", "Informatique", "M2");
        Etudiant e2 = new Etudiant(null, "Cherif",  "Sara",   "sara.cherif@univ.dz",   "20210002", "Informatique", "M2");
        Etudiant e3 = new Etudiant(null, "Hamza",   "Youcef", "youcef.hamza@univ.dz",  "20210003", "ISIL",         "M2");
        etudiantRepo.save(e1);
        etudiantRepo.save(e2);
        etudiantRepo.save(e3);

        // ── Encadrants ───────────────────────────────────────────────────
        Encadrant enc1 = new Encadrant(null, "Meziane",  "Karim",   "k.meziane@univ.dz",  "MCB", "IA");
        Encadrant enc2 = new Encadrant(null, "Boudiaf",  "Lynda",   "l.boudiaf@univ.dz",  "MCA", "Réseaux");
        Encadrant enc3 = new Encadrant(null, "Tebbal",   "Samir",   "s.tebbal@univ.dz",   "PR",  "BD");
        Encadrant enc4 = new Encadrant(null, "Ouznadji", "Fatima",  "f.ouznadji@univ.dz", "MCB", "GL");
        encadrantRepo.save(enc1);
        encadrantRepo.save(enc2);
        encadrantRepo.save(enc3);
        encadrantRepo.save(enc4);

        // ── Salles ───────────────────────────────────────────────────────
        Salle s1 = new Salle(null, "Salle A101", 20, "Bâtiment A, RDC", true);
        Salle s2 = new Salle(null, "Salle B205", 15, "Bâtiment B, 2ème étage", true);
        Salle s3 = new Salle(null, "Amphi 1",    50, "Bâtiment Principal",     true);
        salleRepo.save(s1);
        salleRepo.save(s2);
        salleRepo.save(s3);

        // ── Soutenances planifiées ───────────────────────────────────────
        Soutenance sout1 = new Soutenance();
        sout1.setTitre("Système de recommandation basé sur le ML");
        sout1.setDateHeure(LocalDateTime.now().plusDays(7).withHour(9).withMinute(0));
        sout1.setDureeMinutes(30);
        sout1.setEtudiant(e1);
        sout1.setEncadrant(enc1);
        sout1.setSalle(s1);
        sout1.setStatut(Soutenance.StatutSoutenance.PLANIFIEE);
        soutenanceRepo.save(sout1);

        Soutenance sout2 = new Soutenance();
        sout2.setTitre("Application IoT pour la gestion agricole");
        sout2.setDateHeure(LocalDateTime.now().plusDays(7).withHour(10).withMinute(0));
        sout2.setDureeMinutes(30);
        sout2.setEtudiant(e2);
        sout2.setEncadrant(enc2);
        sout2.setSalle(s2);
        sout2.setStatut(Soutenance.StatutSoutenance.PLANIFIEE);
        soutenanceRepo.save(sout2);

        System.out.println("\n========================================");
        System.out.println("  ✅ Données de test initialisées");
        System.out.println("  🌐 API : http://localhost:8081/api");
        System.out.println("  🐘 DB  : PostgreSQL (soutenances_db)");
        System.out.println("========================================\n");
    }
}

