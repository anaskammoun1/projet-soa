package com.soutenance.features.resultat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resultats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resultat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Long etudiantId;

    @Column(nullable = false, unique = true)
    private Long soutenanceId;

    @Column(nullable = false)
    private Double moyenneFinale;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Mention mention;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Decision decisionFinale;

    @Builder.Default
    @Column(nullable = false)
    private Boolean valide = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean publie = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Mention {
        SANS_MENTION, PASSABLE, ASSEZ_BIEN, BIEN, TRES_BIEN
    }

    public enum Decision {
        ADMIS, AJOURNE
    }
}
