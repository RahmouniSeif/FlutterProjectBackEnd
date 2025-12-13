package com.campify.campifybackend.service;

import com.campify.campifybackend.dto.LoginRequest;
import com.campify.campifybackend.model.User;
import com.campify.campifybackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // CREATE : Crée un nouvel utilisateur
    public User createUser(User user) {
        // Logique métier avant l'enregistrement (ex: validation, hachage du mot de passe)
        // Note: Assurez-vous que le champ password est haché avant de le sauvegarder en BDD
        return userRepository.save(user);
    }

    // READ : Récupère tous les utilisateurs
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // READ : Récupère un utilisateur par ID
    public Optional<User> findUserById(UUID id) {
        return userRepository.findById(id);
    }

    // UPDATE : Met à jour un utilisateur existant
    public User updateUser(UUID id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    // Mise à jour des champs modifiables
                    user.setName(updatedUser.getName());
                    user.setEmail(updatedUser.getEmail());
                    user.setPhone(updatedUser.getPhone());
                    user.setUserType(updatedUser.getUserType());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    // DELETE : Supprime un utilisateur
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }


    // --- LOGIN/AUTHENTICATION METHOD (Now using JpaRepository) ---
    public Optional<User> login(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        // 1. Find the user by email using the custom repository method
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 2. Check if the provided password matches the stored password.
            // WARNING: For production, replace this simple 'equals' with
            // a secure password encoder (e.g., BCryptPasswordEncoder) comparison!
            if (user.getPassword() != null && user.getPassword().equals(password)) {
                // Authentication successful
                return userOptional;
            }
        }

        // Authentication failed (user not found or password mismatch)
        return Optional.empty();
    }

    // --- Forgot Password ---
    @Autowired
    private EmailService emailService;

    public void forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Generate 4-digit token
            String token = String.valueOf(1000 + new java.util.Random().nextInt(9000));
            user.setResetToken(token);
            // Token valid for 1 hour
            user.setResetTokenExpiry(java.time.OffsetDateTime.now().plusHours(1));
            userRepository.save(user);

            // Send email
            emailService.sendSimpleMessage(
                    user.getEmail(),
                    "Password Reset Request",
                    "To reset your password, use this token: " + token
            );
        } else {
            throw new RuntimeException("there is no user with this email !");
        }
    }

    // --- Reset Password ---
    public boolean resetPassword(String token, String newPassword) {
        // Find user by token? Effectively we might need a method in repo to find by resetToken
        // Or we assume the user provides email + token?
        // Usually: findByResetToken. Let's assume we can add that to repo or iterate (inefficient)
        // Better: Add findByResetToken to UserRepository.
        
        Optional<User> userOptional = userRepository.findByResetToken(token);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Check expiry
            if (user.getResetTokenExpiry().isAfter(java.time.OffsetDateTime.now())) {
                user.setPassword(newPassword); // Remember to hash this in production!
                user.setResetToken(null);
                user.setResetTokenExpiry(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    // --- Change Password ---
    public boolean changePassword(UUID userId, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Verify old password
            // WARNING: In production use passwordEncoder.matches(oldPassword, user.getPassword())
            if (user.getPassword() != null && user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword); // Hash this!
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
}