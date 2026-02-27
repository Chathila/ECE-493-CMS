package com.ece493.cms.service;

import com.ece493.cms.model.LoginResult;
import com.ece493.cms.model.LoginSubmission;
import com.ece493.cms.model.UserAccount;
import com.ece493.cms.repository.UserAccountRepository;
import com.ece493.cms.security.PasswordHasher;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordHasher passwordHasher;
    private final AccountStatusService accountStatusService;
    private final AuthenticationAvailabilityService availabilityService;

    public AuthenticationServiceImpl(
            UserAccountRepository userAccountRepository,
            PasswordHasher passwordHasher,
            AccountStatusService accountStatusService,
            AuthenticationAvailabilityService availabilityService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordHasher = passwordHasher;
        this.accountStatusService = accountStatusService;
        this.availabilityService = availabilityService;
    }

    @Override
    public LoginResult authenticate(LoginSubmission submission) {
        if (submission == null || isBlank(submission.getEmail()) || isBlank(submission.getPassword())) {
            return LoginResult.error(400, "Missing required fields: email and password are required.");
        }

        if (!availabilityService.isAvailable()) {
            return LoginResult.error(503, "Authentication service unavailable. Please try again later.");
        }

        Optional<UserAccount> accountOptional = userAccountRepository.findByEmail(submission.getEmail().trim());
        if (accountOptional.isEmpty()) {
            return LoginResult.error(401, "Authentication failed: incorrect email or password.");
        }

        UserAccount account = accountOptional.get();
        if (!passwordHasher.matches(submission.getPassword(), account.getPasswordSalt(), account.getPasswordHash())) {
            return LoginResult.error(401, "Authentication failed: incorrect email or password.");
        }

        if (!accountStatusService.allowsLogin(account.getStatus())) {
            return LoginResult.error(403, "Account is inactive or locked. Access denied.");
        }

        String role = account.getRole() == null ? "USER" : account.getRole();
        String encodedRole = URLEncoder.encode(role, StandardCharsets.UTF_8);
        return LoginResult.successRedirect("/home?role=" + encodedRole);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
