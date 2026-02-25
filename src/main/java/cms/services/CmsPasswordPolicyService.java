package cms.services;

import java.util.Optional;
import java.util.regex.Pattern;

public class CmsPasswordPolicyService implements PasswordPolicyService {
    private static final Pattern UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL = Pattern.compile(".*[^A-Za-z0-9].*");

    @Override
    public Optional<String> validationError(String password) {
        if (password == null || password.length() < 8) {
            return Optional.of(RegistrationMessages.WEAK_PASSWORD);
        }
        if (!UPPERCASE.matcher(password).matches()) {
            return Optional.of(RegistrationMessages.WEAK_PASSWORD);
        }
        if (!LOWERCASE.matcher(password).matches()) {
            return Optional.of(RegistrationMessages.WEAK_PASSWORD);
        }
        if (!DIGIT.matcher(password).matches()) {
            return Optional.of(RegistrationMessages.WEAK_PASSWORD);
        }
        if (!SPECIAL.matcher(password).matches()) {
            return Optional.of(RegistrationMessages.WEAK_PASSWORD);
        }
        return Optional.empty();
    }
}
