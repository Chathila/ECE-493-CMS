package com.ece493.cms.service;

import com.ece493.cms.model.RegistrationResult;
import com.ece493.cms.model.UserAccount;
import com.ece493.cms.repository.UserAccountRepository;
import com.ece493.cms.security.PasswordHasher;

import java.time.Instant;

public class RegistrationServiceImpl implements RegistrationService {
    private final UserAccountRepository userAccountRepository;
    private final EmailValidationService emailValidationService;
    private final PasswordPolicyService passwordPolicyService;
    private final PasswordHasher passwordHasher;

    public RegistrationServiceImpl(
            UserAccountRepository userAccountRepository,
            EmailValidationService emailValidationService,
            PasswordPolicyService passwordPolicyService,
            PasswordHasher passwordHasher
    ) {
        this.userAccountRepository = userAccountRepository;
        this.emailValidationService = emailValidationService;
        this.passwordPolicyService = passwordPolicyService;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public RegistrationResult register(String email, String password) {
        if (isBlank(email) || isBlank(password)) {
            return RegistrationResult.error(400, "Missing required fields: email and password are required.");
        }

        if (!emailValidationService.isServiceAvailable()) {
            return RegistrationResult.error(503, "Email validation service unavailable. Please try again later.");
        }

        if (!emailValidationService.isValidFormat(email)) {
            return RegistrationResult.error(400, "Please provide a valid email address.");
        }

        if (userAccountRepository.existsByEmail(email)) {
            return RegistrationResult.error(409, "Email address is already registered. Please use a unique email.");
        }

        if (!passwordPolicyService.meetsPolicy(password)) {
            return RegistrationResult.error(400, "Password does not meet CMS policy. Use at least 8 characters, with uppercase, lowercase, and a digit.");
        }

        String salt = passwordHasher.generateSalt();
        String hash = passwordHasher.hashPassword(password, salt);

        userAccountRepository.save(new UserAccount(
                0L,
                email,
                hash,
                salt,
                "ACTIVE",
                Instant.now()
        ));

        return RegistrationResult.successRedirect("/login");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
