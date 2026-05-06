SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM resultats;
DELETE FROM soutenances;
DELETE FROM users;
DELETE FROM etudiants;
DELETE FROM enseignants;
DELETE FROM salles;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO enseignants (id, nom, prenom, email, grade, specialite) VALUES
(1, 'Jebali', 'Nadia', 'nadia.jebali@gmail.com', 'Professeur', 'Architecture logicielle'),
(2, 'Mrad', 'Karim', 'karim.mrad@outlook.com', 'Maitre de conferences', 'Systemes distribues'),
(3, 'Bouzid', 'Amira', 'amira.bouzid@gmail.com', 'Maitre assistante', 'Intelligence artificielle'),
(4, 'Chebbi', 'Youssef', 'youssef.chebbi@outlook.com', 'Professeur', 'Cybersecurite'),
(5, 'Guesmi', 'Rania', 'rania.guesmi@gmail.com', 'Assistante', 'Bases de donnees'),
(6, 'Triki', 'Mehdi', 'mehdi.triki@outlook.com', 'Docteur', 'Cloud computing'),
(7, 'Ferchichi', 'Salma', 'salma.ferchichi@gmail.com', 'Maitre assistante', 'Genie logiciel'),
(8, 'Khalfaoui', 'Walid', 'walid.khalfaoui@outlook.com', 'Professeur', 'Reseaux et telecoms');

INSERT INTO salles (id, nom, capacite, localisation, disponible) VALUES
(1, 'A101', 24, 'Bloc A - 1er etage', 1),
(2, 'A204', 36, 'Bloc A - 2eme etage', 1),
(3, 'B102', 28, 'Bloc B - Rez-de-chaussee', 1),
(4, 'B305', 50, 'Bloc B - 3eme etage', 1),
(5, 'C210', 18, 'Bloc C - 2eme etage', 0),
(6, 'D014', 42, 'Bloc D - Rez-de-chaussee', 1);

INSERT INTO etudiants (id, nom, prenom, email, matricule, filiere, niveau, encadrant_id) VALUES
(1, 'Sassi', 'Malek', 'malek.sassi@gmail.com', 'GS-2026-001', 'Genie logiciel', 'Master 2', 1),
(2, 'Baccouche', 'Ines', 'ines.baccouche@outlook.com', 'GS-2026-002', 'Data science', 'Master 2', 2),
(3, 'Haddad', 'Tarek', 'tarek.haddad@gmail.com', 'GS-2026-003', 'Cybersecurite', 'Master 2', 4),
(4, 'Mejri', 'Lina', 'lina.mejri@outlook.com', 'GS-2026-004', 'Systemes embarques', 'Master 1', 6),
(5, 'Kacem', 'Omar', 'omar.kacem@gmail.com', 'GS-2026-005', 'Cloud computing', 'Master 2', 3),
(6, 'Mansouri', 'Yasmine', 'yasmine.mansouri@outlook.com', 'GS-2026-006', 'Business intelligence', 'Master 2', 5),
(7, 'Saidi', 'Anis', 'anis.saidi@gmail.com', 'GS-2026-007', 'Reseaux et telecoms', 'Master 1', 8),
(8, 'Ben Salem', 'Nour', 'nour.bensalem@outlook.com', 'GS-2026-008', 'Genie logiciel', 'Master 2', 7),
(9, 'Ayed', 'Firas', 'firas.ayed@gmail.com', 'GS-2026-009', 'Intelligence artificielle', 'Master 2', 3),
(10, 'Zouari', 'Meriem', 'meriem.zouari@outlook.com', 'GS-2026-010', 'Bases de donnees', 'Master 1', 5);

INSERT INTO soutenances (
    id, titre, date, duree, statut, president_id, rapporteur_id, examinateur_id, salle_id, etudiant_id,
    note_president, note_president_expose, note_president_rapport, note_president_questions,
    note_rapporteur, note_rapporteur_expose, note_rapporteur_rapport, note_rapporteur_questions,
    note_examinateur, note_examinateur_expose, note_examinateur_rapport, note_examinateur_questions
) VALUES
(1, 'Plateforme SOA pour la coordination des soutenances', DATE_SUB(NOW(), INTERVAL 25 DAY), 60, 'TERMINEE', 2, 3, 4, 1, 1, 17.67, 18, 17, 18, 16.67, 16, 17, 17, 18.33, 19, 18, 18),
(2, 'Tableau de bord intelligent pour le suivi academique', DATE_SUB(NOW(), INTERVAL 20 DAY), 75, 'TERMINEE', 1, 4, 5, 2, 2, 15.33, 15, 16, 15, 14.67, 14, 15, 15, 16.00, 16, 16, 16),
(3, 'Audit de securite pour applications universitaires', DATE_SUB(NOW(), INTERVAL 16 DAY), 60, 'TERMINEE', 1, 2, 6, 3, 3, 11.33, 11, 12, 11, 10.67, 10, 11, 11, 12.33, 13, 12, 12),
(4, 'Prototype mobile pour gestion des absences', DATE_SUB(NOW(), INTERVAL 12 DAY), 45, 'TERMINEE', 2, 3, 5, 4, 4, 8.67, 9, 8, 9, 9.33, 9, 10, 9, 8.00, 8, 8, 8),
(5, 'Migration cloud progressive d un systeme administratif', DATE_ADD(NOW(), INTERVAL 8 DAY), 60, 'PLANIFIEE', 1, 5, 6, 1, 5, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(6, 'Moteur de recommandation pour choix de sujets', DATE_SUB(NOW(), INTERVAL 20 MINUTE), 90, 'EN_COURS', 2, 4, 7, 6, 6, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(7, 'Supervision reseau pour salles connectees', DATE_ADD(NOW(), INTERVAL 5 DAY), 60, 'ANNULEE', 1, 3, 6, 5, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(8, 'Gestion collaborative des documents de soutenance', DATE_ADD(NOW(), INTERVAL 15 DAY), 60, 'PLANIFIEE', 2, 4, 5, 2, 8, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(9, 'Analyse predictive des resultats de jury', DATE_SUB(NOW(), INTERVAL 6 DAY), 60, 'TERMINEE', 1, 2, 5, 3, 9, 13.67, 14, 13, 14, 14.33, 14, 15, 14, 13.00, 13, 13, 13),
(10, 'Referentiel centralise des salles et disponibilites', DATE_SUB(NOW(), INTERVAL 2 DAY), 60, 'TERMINEE', 1, 3, 4, 4, 10, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO resultats (
    soutenance_id, etudiant_id, moyenne_finale, mention, decision_finale, valide, publie, created_at, updated_at, published_at
) VALUES
(1, 1, 17.56, 'TRES_BIEN', 'ADMIS', 1, 1, DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
(2, 2, 15.33, 'BIEN', 'ADMIS', 1, 0, DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY), NULL),
(3, 3, 11.44, 'PASSABLE', 'ADMIS', 0, 0, DATE_SUB(NOW(), INTERVAL 15 DAY), NULL, NULL),
(4, 4, 8.67, 'SANS_MENTION', 'AJOURNE', 1, 1, DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
(9, 9, 13.67, 'ASSEZ_BIEN', 'ADMIS', 0, 0, DATE_SUB(NOW(), INTERVAL 5 DAY), NULL, NULL);

INSERT INTO users (username, email, password_hash, role, enseignant_id, etudiant_id, enabled) VALUES
('admin', 'admin@example.local', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ADMIN', NULL, NULL, 1),
('teacher.nadia', 'nadia.jebali@gmail.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ENSEIGNANT', 1, NULL, 1),
('teacher.karim', 'karim.mrad@outlook.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ENSEIGNANT', 2, NULL, 1),
('teacher.amira', 'amira.bouzid@gmail.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ENSEIGNANT', 3, NULL, 1),
('teacher.youssef', 'youssef.chebbi@outlook.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ENSEIGNANT', 4, NULL, 1),
('teacher.rania', 'rania.guesmi@gmail.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ENSEIGNANT', 5, NULL, 1),
('teacher.mehdi', 'mehdi.triki@outlook.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ENSEIGNANT', 6, NULL, 1),
('teacher.salma', 'salma.ferchichi@gmail.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ENSEIGNANT', 7, NULL, 1),
('teacher.walid', 'walid.khalfaoui@outlook.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ENSEIGNANT', 8, NULL, 1),
('student.malek', 'malek.sassi@gmail.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ETUDIANT', NULL, 1, 1),
('student.ines', 'ines.baccouche@outlook.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ETUDIANT', NULL, 2, 1),
('student.tarek', 'tarek.haddad@gmail.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ETUDIANT', NULL, 3, 1),
('student.lina', 'lina.mejri@outlook.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ETUDIANT', NULL, 4, 1),
('student.omar', 'omar.kacem@gmail.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ETUDIANT', NULL, 5, 1),
('student.yasmine', 'yasmine.mansouri@outlook.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ETUDIANT', NULL, 6, 1),
('student.anis', 'anis.saidi@gmail.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ETUDIANT', NULL, 7, 1),
('student.nour', 'nour.bensalem@outlook.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ETUDIANT', NULL, 8, 1),
('student.firas', 'firas.ayed@gmail.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ETUDIANT', NULL, 9, 1),
('student.meriem', 'meriem.zouari@outlook.com', '$2a$10$0K.e3b5RLChpaTptO6W1mu4u7mefrr8Nx.YjybYKrjqSx8ioL/faa', 'ETUDIANT', NULL, 10, 1);
