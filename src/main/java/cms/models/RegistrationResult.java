package cms.models;

import java.util.Collections;
import java.util.List;

public final class RegistrationResult {
    private final boolean success;
    private final List<String> errors;
    private final String confirmationMessage;
    private final String redirectPath;

    private RegistrationResult(boolean success, List<String> errors, String confirmationMessage, String redirectPath) {
        this.success = success;
        this.errors = errors;
        this.confirmationMessage = confirmationMessage;
        this.redirectPath = redirectPath;
    }

    public static RegistrationResult success(String confirmationMessage, String redirectPath) {
        return new RegistrationResult(true, Collections.emptyList(), confirmationMessage, redirectPath);
    }

    public static RegistrationResult failure(List<String> errors) {
        return new RegistrationResult(false, List.copyOf(errors), null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getConfirmationMessage() {
        return confirmationMessage;
    }

    public String getRedirectPath() {
        return redirectPath;
    }
}
