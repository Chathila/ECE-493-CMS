package com.ece493.cms.service;

public class DefaultAuthenticationAvailabilityService implements AuthenticationAvailabilityService {
    @Override
    public boolean isAvailable() {
        return true;
    }
}
