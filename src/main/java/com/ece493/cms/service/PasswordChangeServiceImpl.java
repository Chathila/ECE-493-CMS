package com.ece493.cms.service;

import com.ece493.cms.model.PasswordChangeRequest;
import com.ece493.cms.model.PasswordChangeResult;
import com.ece493.cms.model.UserAccount;
import com.ece493.cms.repository.UserAccountRepository;
import com.ece493.cms.security.PasswordHasher;

import java.util.Optional;

public class PasswordChangeServiceImpl implements PasswordChangeService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordPolicyService passwordPolicyService;
    private final PasswordHasher passwordHasher;
    private final AuthenticationAvailabilityService availabilityService;

    public PasswordChangeServiceImpl(
            UserAccountRepository userAccountRepository,
            PasswordPolicyService passwordPolicyService,
            PasswordHasher passwordHasher,
            AuthenticationAvailabilityService availabilityService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordPolicyService = passwordPolicyService;
        this.passwordHasher = passwordHasher;
        this.availabilityService = availabilityService;
    }

    @Override
    public PasswordChangeResult changePassword(String email, PasswordChangeRequest request) {
        if (isBlank(email)) {
            return PasswordChangeResult.error(401, "You must log in to change password.");
        }

        if (request == null
                || isBlank(request.getCurrentPassword())
                || isBlank(request.getNewPassword())
                || isBlank(request.getConfirmPassword())) {
            return PasswordChangeResult.error(400, "Missing required fields: current password, new password, and confirmation are required.");
        }

        if (!availabilityService.isAvailable()) {
            return PasswordChangeResult.error(503, "Authentication service unavailable. Please try again later.");
        }

        Optional<UserAccount> accountOptional = userAccountRepository.findByEmail(email);
        if (accountOptional.isEmpty()) {
            return PasswordChangeResult.error(401, "Current password is invalid.");
        }

        UserAccount account = accountOptional.get();
        if (!passwordHasher.matches(request.getCurrentPassword(), account.getPasswordSalt(), account.getPasswordHash())) {
            return PasswordChangeResult.error(401, "Current password is invalid.");
        }

        if (!passwordPolicyService.meetsPolicy(request.getNewPassword())) {
            return PasswordChangeResult.error(403, "New password does not meet CMS policy. Use at least 8 characters, with uppercase, lowercase, and a digit.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return PasswordChangeResult.error(400, "New password and confirmation must match.");
        }

        String newSalt = passwordHasher.generateSalt();
        String newHash = passwordHasher.hashPassword(request.getNewPassword(), newSalt);
        boolean updated = userAccountRepository.updatePasswordCredentialsByEmail(email, newHash, newSalt);
        if (!updated) {
            return PasswordChangeResult.error(503, "Password change failed. Please try again later.");
        }

        return PasswordChangeResult.success("Password changed successfully. Please log in again.");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
