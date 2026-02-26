package cms.models;

import java.time.Instant;

public final class UserAccount {
    private final String email;
    private final String passwordHash;
    private final String passwordSalt;
    private final AccountStatus status;
    private final UserRole role;
    private final Instant createdAt;

    public UserAccount(String email, String passwordHash, String passwordSalt, boolean active, Instant createdAt) {
        this(email, passwordHash, passwordSalt, active ? AccountStatus.ACTIVE : AccountStatus.INACTIVE, UserRole.AUTHOR, createdAt);
    }

    public UserAccount(String email,
                       String passwordHash,
                       String passwordSalt,
                       AccountStatus status,
                       UserRole role,
                       Instant createdAt) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.status = status;
        this.role = role;
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
        return status == AccountStatus.ACTIVE;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public UserRole getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
