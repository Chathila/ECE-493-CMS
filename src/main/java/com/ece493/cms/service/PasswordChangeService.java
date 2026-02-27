package com.ece493.cms.service;

import com.ece493.cms.model.PasswordChangeRequest;
import com.ece493.cms.model.PasswordChangeResult;

public interface PasswordChangeService {
    PasswordChangeResult changePassword(String email, PasswordChangeRequest request);
}
