package cms.services;

public interface PasswordHasher {
    HashResult hash(String plainTextPassword);

    default boolean verify(String plainTextPassword, String storedHash, String storedSalt) {
        throw new UnsupportedOperationException("Password verification not supported");
    }
}
