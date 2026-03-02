package com.ece493.cms.model;

import java.util.List;

public class RegistrationPriceViewResult {
    private final int statusCode;
    private final String message;
    private final List<RegistrationPrice> prices;

    private RegistrationPriceViewResult(int statusCode, String message, List<RegistrationPrice> prices) {
        this.statusCode = statusCode;
        this.message = message;
        this.prices = prices == null ? List.of() : List.copyOf(prices);
    }

    public static RegistrationPriceViewResult found(List<RegistrationPrice> prices) {
        return new RegistrationPriceViewResult(200, "Registration prices loaded.", prices);
    }

    public static RegistrationPriceViewResult error(int statusCode, String message) {
        return new RegistrationPriceViewResult(statusCode, message, List.of());
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public List<RegistrationPrice> getPrices() {
        return prices;
    }
}
