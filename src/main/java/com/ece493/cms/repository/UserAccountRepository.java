package com.ece493.cms.repository;

import com.ece493.cms.model.UserAccount;

public interface UserAccountRepository {
    boolean existsByEmail(String email);

    void save(UserAccount userAccount);

    long countByEmail(String email);
}
