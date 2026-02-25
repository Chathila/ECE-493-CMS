package cms.persistence;

import cms.models.UserAccount;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, UserAccount> users = new ConcurrentHashMap<>();

    @Override
    public boolean existsByEmail(String email) {
        return users.containsKey(normalizeEmail(email));
    }

    @Override
    public void save(UserAccount account) {
        users.put(normalizeEmail(account.getEmail()), account);
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return Optional.ofNullable(users.get(normalizeEmail(email)));
    }

    @Override
    public long count() {
        return users.size();
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
