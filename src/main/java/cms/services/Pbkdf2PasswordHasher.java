package cms.services;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Pbkdf2PasswordHasher implements PasswordHasher {
    private static final int SALT_BYTES = 16;
    private static final int ITERATIONS = 65_536;
    private static final int KEY_LENGTH_BITS = 256;

    private final SecureRandom secureRandom;

    public Pbkdf2PasswordHasher() {
        this(new SecureRandom());
    }

    public Pbkdf2PasswordHasher(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    @Override
    public HashResult hash(String plainTextPassword) {
        byte[] salt = new byte[SALT_BYTES];
        secureRandom.nextBytes(salt);

        try {
            PBEKeySpec keySpec = new PBEKeySpec(plainTextPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = keyFactory.generateSecret(keySpec).getEncoded();
            return new HashResult(Base64.getEncoder().encodeToString(hash), Base64.getEncoder().encodeToString(salt));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new IllegalStateException("Failed to hash password", ex);
        }
    }
}
