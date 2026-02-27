package com.ece493.cms.service;

public interface PasswordPolicyService {
    boolean meetsPolicy(String password);
}
