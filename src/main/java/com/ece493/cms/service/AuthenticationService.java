package com.ece493.cms.service;

import com.ece493.cms.model.LoginResult;
import com.ece493.cms.model.LoginSubmission;

public interface AuthenticationService {
    LoginResult authenticate(LoginSubmission submission);
}
