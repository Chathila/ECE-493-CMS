package com.ece493.cms.service;

import com.ece493.cms.model.RegistrationResult;

public interface RegistrationService {
    RegistrationResult register(String name, String email, String password);
}
