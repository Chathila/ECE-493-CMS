package cms.models;

import java.util.Collections;
import java.util.List;

public final class LoginResult {
    private final boolean success;
    private final List<String> errors;
    private final String redirectPath;

    private LoginResult(boolean success, List<String> errors, String redirectPath) {
        this.success = success;
        this.errors = errors;
        this.redirectPath = redirectPath;
    }

    public static LoginResult success(String redirectPath) {
        return new LoginResult(true, Collections.emptyList(), redirectPath);
    }

    public static LoginResult failure(List<String> errors) {
        return new LoginResult(false, List.copyOf(errors), null);
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getRedirectPath() {
        return redirectPath;
    }
}
