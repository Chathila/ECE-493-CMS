package com.ece493.cms.model;

import java.time.Instant;

public class UserAccount {
    private final long userId;
    private final String email;
    private final String passwordHash;
    private final String passwordSalt;
    private final String status;
    private final Instant createdAt;

    public UserAccount(long userId, String email, String passwordHash, String passwordSalt, String status, Instant createdAt) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.status = status;
        this.createdAt = createdAt;
    }

    public long getUserId() {
        return userId;
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

    public Instant getCreatedAt() {
        return createdAt;
    }
}
