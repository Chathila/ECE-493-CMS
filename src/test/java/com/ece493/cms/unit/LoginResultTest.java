package com.ece493.cms.unit;

import com.ece493.cms.model.LoginResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginResultTest {
    @Test
    void isRedirectIsFalseForError() {
        assertFalse(LoginResult.error(401, "bad").isRedirect());
    }

    @Test
    void isRedirectIsTrueForSuccessRedirect() {
        assertTrue(LoginResult.successRedirect("/home").isRedirect());
    }
}
