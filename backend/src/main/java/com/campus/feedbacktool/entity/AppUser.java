package com.campus.feedbacktool.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Every AppUser is an admin - there is no self-registration and no other
 * role. Named AppUser (not User) to avoid clashing with the reserved word
 * USER in some SQL dialects / H2's information schema.
 */
@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 60)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password; // BCrypt-hashed, never stored or returned as plaintext

    public AppUser() {
    }

    public AppUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
