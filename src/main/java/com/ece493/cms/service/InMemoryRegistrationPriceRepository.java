package com.ece493.cms.service;

import com.ece493.cms.model.RegistrationPrice;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRegistrationPriceRepository implements RegistrationPriceRepository {
    private List<RegistrationPrice> prices = new ArrayList<>();
    private boolean failOnRead;

    @Override
    public List<RegistrationPrice> findAll() {
        if (failOnRead) {
            throw new IllegalStateException("Failed to retrieve pricing");
        }
        return List.copyOf(prices);
    }

    public void setPrices(List<RegistrationPrice> prices) {
        this.prices = prices == null ? List.of() : new ArrayList<>(prices);
    }

    public void setFailOnRead(boolean failOnRead) {
        this.failOnRead = failOnRead;
    }
}
