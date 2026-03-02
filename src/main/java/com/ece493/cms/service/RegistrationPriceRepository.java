package com.ece493.cms.service;

import com.ece493.cms.model.RegistrationPrice;

import java.util.List;

public interface RegistrationPriceRepository {
    List<RegistrationPrice> findAll();
}
