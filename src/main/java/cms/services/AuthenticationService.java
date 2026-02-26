package cms.services;

import cms.models.LoginRequest;
import cms.models.LoginResult;

public interface AuthenticationService {
    LoginResult login(LoginRequest request);
}
