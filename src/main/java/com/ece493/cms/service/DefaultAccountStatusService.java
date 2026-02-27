package com.ece493.cms.service;

public class DefaultAccountStatusService implements AccountStatusService {
    @Override
    public boolean allowsLogin(String status) {
        return "ACTIVE".equalsIgnoreCase(status);
    }
}
