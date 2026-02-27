package com.ece493.cms.unit;

import com.ece493.cms.security.PasswordHasher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {
    @Test
    void hashesPasswordWithSalt() {
        PasswordHasher hasher = new PasswordHasher();
        String salt = hasher.generateSalt();
        String hashOne = hasher.hashPassword("Valid123", salt);
        String hashTwo = hasher.hashPassword("Valid123", salt);

        assertNotNull(salt);
        assertNotNull(hashOne);
        assertNotEquals("Valid123", hashOne);
        assertEquals(hashOne, hashTwo);
    }
}
