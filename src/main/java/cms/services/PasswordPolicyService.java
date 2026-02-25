package cms.services;

import java.util.Optional;

public interface PasswordPolicyService {
    Optional<String> validationError(String password);
}
