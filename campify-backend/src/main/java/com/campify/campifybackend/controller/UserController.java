package com.campify.campifybackend.controller;

import com.campify.campifybackend.dto.LoginRequest;
import com.campify.campifybackend.model.User;
import com.campify.campifybackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /api/v1/users/login
     * Authenticates a user based on email and password using the UserService.
     * @param loginRequest DTO containing user credentials.
     * @return ResponseEntity<User> with 200 OK on success, 401 UNAUTHORIZED on failure.
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest)
                .map(ResponseEntity::ok) // 200 OK with User body on success
                .orElseGet(() ->
                        // 401 UNAUTHORIZED on authentication failure
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                );
    }


    // GET /api/v1/users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    // GET /api/v1/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/v1/users
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Enregistrement de l'utilisateur
        User createdUser = userService.createUser(user);
        // Retourne un statut 201 CREATED
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // PUT /api/v1/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            // Gérer le cas où l'utilisateur n'est pas trouvé
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/v1/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        try {
            userService.findUserById(id).ifPresentOrElse(
                    user -> userService.deleteUser(id),
                    () -> { throw new RuntimeException("User not found for deletion."); }
            );
            // Retourne un statut 204 NO CONTENT après suppression réussie
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}