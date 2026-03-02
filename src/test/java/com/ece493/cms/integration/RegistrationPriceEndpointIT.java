package com.ece493.cms.integration;

import com.ece493.cms.model.RegistrationPrice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationPriceEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void returnsPriceListByCategory() throws Exception {
        registrationPriceRepository.setPrices(List.of(
                new RegistrationPrice(1L, "student", 120.0),
                new RegistrationPrice(2L, "regular", 300.0)
        ));

        ServletHttpTestSupport.ResponseCapture response = getRegistrationPrices();

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("student"));
        assertTrue(response.getBody().contains("regular"));
    }

    @Test
    void returnsUnavailableAndFailure() throws Exception {
        ServletHttpTestSupport.ResponseCapture unavailable = getRegistrationPrices();
        assertEquals(404, unavailable.getStatus());

        registrationPriceRepository.setFailOnRead(true);
        ServletHttpTestSupport.ResponseCapture failure = getRegistrationPrices();
        assertEquals(500, failure.getStatus());
        assertTrue(failure.getBody().contains("try again later"));
    }
}
