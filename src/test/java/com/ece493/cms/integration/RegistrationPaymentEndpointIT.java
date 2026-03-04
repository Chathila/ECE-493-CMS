package com.ece493.cms.integration;

import com.ece493.cms.service.PaymentServiceDecision;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationPaymentEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void requiresLoginAndSupportsSuccessAndDecline() throws Exception {
        ServletHttpTestSupport.ResponseCapture unauthenticated = postRegistrationPayment(
                "{\"registration_type\":\"regular\",\"payment_details\":{\"method\":\"card\"}}",
                null
        );
        assertEquals(401, unauthenticated.getStatus());

        setPaymentDecision(PaymentServiceDecision.APPROVED);
        ServletHttpTestSupport.ResponseCapture ok = postRegistrationPayment(
                "{\"registration_type\":\"regular\",\"payment_details\":{\"method\":\"card\"}}",
                loggedInSession("attendee@cms.com")
        );
        assertEquals(200, ok.getStatus());
        assertTrue(ok.getBody().contains("payment_id"));

        setPaymentDecision(PaymentServiceDecision.DECLINED);
        ServletHttpTestSupport.ResponseCapture declined = postRegistrationPayment(
                "{\"registration_type\":\"regular\",\"payment_details\":{\"method\":\"card\"}}",
                loggedInSession("attendee@cms.com")
        );
        assertEquals(402, declined.getStatus());
    }
}
