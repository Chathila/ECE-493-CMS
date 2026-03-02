package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.RegistrationPrice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC18AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_viewRegistrationPricesSuccessfully() throws Exception {
        registrationPriceRepository.setPrices(List.of(
                new RegistrationPrice(1L, "student", 120.0),
                new RegistrationPrice(2L, "author", 250.0)
        ));

        ServletHttpTestSupport.ResponseCapture response = getRegistrationPrices();

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("student"));
        assertTrue(response.getBody().contains("author"));
    }

    @Test
    void AT02_registrationPricingInformationNotAvailable() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = getRegistrationPrices();

        assertEquals(404, response.getStatus());
        assertTrue(response.getBody().contains("currently unavailable"));
    }

    @Test
    void AT03_databaseErrorDuringPriceRetrieval() throws Exception {
        registrationPriceRepository.setFailOnRead(true);

        ServletHttpTestSupport.ResponseCapture response = getRegistrationPrices();

        assertEquals(500, response.getStatus());
        assertTrue(response.getBody().contains("try again later"));
    }
}
