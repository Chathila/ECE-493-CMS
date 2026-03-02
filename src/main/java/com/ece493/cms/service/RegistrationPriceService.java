package com.ece493.cms.service;

import com.ece493.cms.model.RegistrationPrice;
import com.ece493.cms.model.RegistrationPriceViewResult;

import java.util.List;

public class RegistrationPriceService {
    private final RegistrationPriceRepository registrationPriceRepository;

    public RegistrationPriceService(RegistrationPriceRepository registrationPriceRepository) {
        this.registrationPriceRepository = registrationPriceRepository;
    }

    public RegistrationPriceViewResult viewPrices() {
        List<RegistrationPrice> prices;
        try {
            prices = registrationPriceRepository.findAll();
        } catch (IllegalStateException e) {
            return RegistrationPriceViewResult.error(500, "Unable to retrieve pricing; please try again later.");
        }

        if (prices.isEmpty()) {
            return RegistrationPriceViewResult.error(404, "Pricing information is currently unavailable.");
        }

        return RegistrationPriceViewResult.found(prices);
    }
}
