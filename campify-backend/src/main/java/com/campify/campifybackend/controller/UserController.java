package com.campify.campifybackend.controller;

import com.campify.campifybackend.dto.LoginRequest;
import com.campify.campifybackend.model.User;
import com.campify.campifybackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation; // Import for endpoint naming
import io.swagger.v3.oas.annotations.tags.Tag; // Import for grouping
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
// Add Tag to group all user-related operations in Swagger UI
@Tag(name = "User Management", description = "Operations related to user accounts and authentication.")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- Login Endpoint ---
    @Operation(
            summary = "Authenticate User Login",
            description = "Authenticates a user based on email and password and returns the User object upon success."
    )
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                );
    }

    // --- Get All Users Endpoint ---
    @Operation(
            summary = "Retrieve All Users",
            description = "Returns a list of all users registered in the system."
    )
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    // --- Get User By ID Endpoint ---
    @Operation(
            summary = "Retrieve User by ID",
            description = "Returns a specific user based on their unique UUID identifier. Returns 404 if not found."
    )
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- Create User Endpoint ---
    @Operation(
            summary = "Create New User",
            description = "Registers a new user in the system. Returns the created user with a 201 status."
    )
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // --- Update User Endpoint ---
    @Operation(
            summary = "Update Existing User",
            description = "Updates the details of an existing user specified by the ID. Returns 404 if the user is not found."
    )
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Delete User Endpoint ---
    @Operation(
            summary = "Delete User by ID",
            description = "Deletes a user account specified by the ID. Returns 204 No Content on success or 404 if the user is not found."
    )
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        try {
            userService.findUserById(id).ifPresentOrElse(
                    user -> userService.deleteUser(id),
                    () -> { throw new RuntimeException("User not found for deletion."); }
            );
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Forgot Password ---
    @Operation(summary = "Request Password Reset", description = "Generates a reset token and sends it to the user's email.")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody java.util.Map<String, String> body) {
        String email = body.get("email");
        try {
            userService.forgotPassword(email);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // --- Reset Password ---
    @Operation(summary = "Reset Password", description = "Resets the user's password using a valid token.")
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody java.util.Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        
        if (userService.resetPassword(token, newPassword)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}