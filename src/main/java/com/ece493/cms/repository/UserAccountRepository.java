package com.ece493.cms.repository;

import com.ece493.cms.model.UserAccount;

import java.util.Optional;

public interface UserAccountRepository {
    boolean existsByEmail(String email);

    Optional<UserAccount> findByEmail(String email);

    void save(UserAccount userAccount);

    boolean updatePasswordCredentialsByEmail(String email, String passwordHash, String passwordSalt);

    long countByEmail(String email);
}
