package com.campify.campifybackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

// Lombok annotations pour générer les getters/setters/constructeur sans arguments
@Data
@NoArgsConstructor
@Entity
@Table(name = "users") // Correspond au nom de la table en PostgreSQL
public class User {

    // Clé Primaire UUID, générée par la base de données
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    // ATTENTION: Le mot de passe devrait être géré par un mécanisme de hachage (ex: Spring Security)
    @Column(name = "password", nullable = false)
    private String password;

    // JSONB: Spring Data JPA/Hibernate peut gérer le JSONB.
    // Pour les besoins de ce CRUD simple, nous le traitons comme une String.
    // Pour une manipulation complexe en JSON, il faudrait une configuration Hibernate Type spécifique.
    @Column(name = "preferences", columnDefinition = "jsonb")
    private String preferences;

    @Column(name = "user_type", nullable = false)
    private String userType; // 'user' ou 'center_owner'

    @CreationTimestamp // Gère l'insertion automatique du timestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // Constructeur pour la création (sans l'ID ni la date de création)
    public User(String name, String email, String phone, String password, String preferences, String userType) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.preferences = preferences;
        this.userType = userType;
    }
}