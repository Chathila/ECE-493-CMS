package com.ece493.cms.model;

public class RegistrationPrice {
    private final long priceId;
    private final String category;
    private final double amount;

    public RegistrationPrice(long priceId, String category, double amount) {
        this.priceId = priceId;
        this.category = category;
        this.amount = amount;
    }

    public long getPriceId() {
        return priceId;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
}
