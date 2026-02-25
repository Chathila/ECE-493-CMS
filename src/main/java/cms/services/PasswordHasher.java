package cms.services;

public interface PasswordHasher {
    HashResult hash(String plainTextPassword);
}
