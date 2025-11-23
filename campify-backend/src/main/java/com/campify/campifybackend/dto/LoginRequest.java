package com.campify.campifybackend.dto;

import java.io.Serializable;

// DTO representing the payload for a login request
public class LoginRequest implements Serializable {

    private String email;
    private String password;

    // Default constructor for Jackson
    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Optional: toString, equals, and hashCode methods can be added
}