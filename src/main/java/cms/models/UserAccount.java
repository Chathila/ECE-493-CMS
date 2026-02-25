package cms.models;

import java.time.Instant;

public final class UserAccount {
    private final String email;
    private final String passwordHash;
    private final String passwordSalt;
    private final boolean active;
    private final Instant createdAt;

    public UserAccount(String email, String passwordHash, String passwordSalt, boolean active, Instant createdAt) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.active = active;
        this.createdAt = createdAt;
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

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
