package com.campify.campifybackend.repository;

import com.campify.campifybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

// JpaRepository fournit automatiquement le CRUD (save, findById, findAll, delete, etc.)
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Exemple de méthode personnalisée pour la recherche par email
    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);
}