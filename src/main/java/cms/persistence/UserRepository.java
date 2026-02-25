package cms.persistence;

import cms.models.UserAccount;

import java.util.Optional;

public interface UserRepository {
    boolean existsByEmail(String email);

    void save(UserAccount account);

    Optional<UserAccount> findByEmail(String email);

    long count();
}
