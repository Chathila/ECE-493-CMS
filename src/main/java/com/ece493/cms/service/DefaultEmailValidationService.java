package com.ece493.cms.service;

import java.util.regex.Pattern;

public class DefaultEmailValidationService implements EmailValidationService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Override
    public boolean isServiceAvailable() {
        return true;
    }

    @Override
    public boolean isValidFormat(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
