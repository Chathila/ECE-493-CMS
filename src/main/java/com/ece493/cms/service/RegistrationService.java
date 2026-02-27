package com.ece493.cms.service;

import com.ece493.cms.model.RegistrationResult;

public interface RegistrationService {
    RegistrationResult register(String email, String password);
}
