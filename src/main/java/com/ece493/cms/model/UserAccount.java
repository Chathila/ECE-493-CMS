package com.ece493.cms.model;

import java.time.Instant;

public class UserAccount {
    private final long userId;
    private final String fullName;
    private final String email;
    private final String passwordHash;
    private final String passwordSalt;
    private final String status;
    private final String role;
    private final Instant createdAt;

    public UserAccount(long userId, String email, String passwordHash, String passwordSalt, String status, Instant createdAt) {
        this(userId, email, passwordHash, passwordSalt, status, "AUTHOR", null, createdAt);
    }

    public UserAccount(long userId, String email, String passwordHash, String passwordSalt, String status, String role, Instant createdAt) {
        this(userId, email, passwordHash, passwordSalt, status, role, null, createdAt);
    }

    public UserAccount(long userId, String email, String passwordHash, String passwordSalt, String status, String role, String fullName, Instant createdAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.status = status;
        this.role = role;
        this.createdAt = createdAt;
    }

    public long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public String getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
