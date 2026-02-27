package com.ece493.cms.service;

public interface EmailValidationService {
    boolean isServiceAvailable();

    boolean isValidFormat(String email);
}
