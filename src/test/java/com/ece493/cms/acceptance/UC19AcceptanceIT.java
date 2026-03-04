package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.service.PaymentServiceDecision;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC19AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_successfulOnlinePayment() throws Exception {
        setPaymentDecision(PaymentServiceDecision.APPROVED);
        ServletHttpTestSupport.ResponseCapture response = postRegistrationPayment(
                "{\"registration_type\":\"student\",\"payment_details\":{\"method\":\"card\"}}",
                loggedInSession("attendee@cms.com")
        );

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"status\":\"confirmed\""));
        assertTrue(response.getBody().contains("Payment was successful."));
    }

    @Test
    void AT02_invalidOrIncompletePaymentInformation() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRegistrationPayment(
                "{\"registration_type\":\"student\",\"payment_details\":{}}",
                loggedInSession("attendee@cms.com")
        );

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("correct payment details"));
    }

    @Test
    void AT03_paymentDeclinedByPaymentService() throws Exception {
        setPaymentDecision(PaymentServiceDecision.DECLINED);
        ServletHttpTestSupport.ResponseCapture response = postRegistrationPayment(
                "{\"registration_type\":\"student\",\"payment_details\":{\"method\":\"card\"}}",
                loggedInSession("attendee@cms.com")
        );

        assertEquals(402, response.getStatus());
        assertTrue(response.getBody().contains("declined"));
        assertEquals(0, paymentRepository.count());
    }

    @Test
    void AT04_paymentServiceUnavailable() throws Exception {
        setPaymentDecision(PaymentServiceDecision.UNAVAILABLE);
        ServletHttpTestSupport.ResponseCapture response = postRegistrationPayment(
                "{\"registration_type\":\"student\",\"payment_details\":{\"method\":\"card\"}}",
                loggedInSession("attendee@cms.com")
        );

        assertEquals(503, response.getStatus());
        assertTrue(response.getBody().contains("try again later"));
        assertEquals(0, paymentRepository.count());
    }
}
