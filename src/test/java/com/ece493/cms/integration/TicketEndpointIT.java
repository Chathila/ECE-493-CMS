package com.ece493.cms.integration;

import com.ece493.cms.service.PaymentServiceDecision;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void createsTicketHandlesFailuresAndRetrievesTicket() throws Exception {
        String paymentId = successfulPaymentId();

        ticketDeliveryService.setFailInSystem(true);
        ServletHttpTestSupport.ResponseCapture failed = postGenerateTicket(paymentId);
        assertEquals(500, failed.getStatus());
        String ticketId = extractField(failed.getBody(), "ticket_id");
        assertEquals(1, ticketFailureRepository.findByTicketId(ticketId).size());

        ServletHttpTestSupport.ResponseCapture view = getTicket(ticketId);
        assertEquals(200, view.getStatus());
        assertTrue(view.getBody().contains("content"));

        ServletHttpTestSupport.ResponseCapture missing = getTicket("999");
        assertEquals(404, missing.getStatus());
    }

    private String successfulPaymentId() throws Exception {
        setPaymentDecision(PaymentServiceDecision.APPROVED);
        ServletHttpTestSupport.ResponseCapture payment = postRegistrationPayment(
                "{\"registration_type\":\"student\",\"payment_details\":{\"method\":\"card\"}}",
                loggedInSession("attendee@cms.com")
        );
        assertEquals(200, payment.getStatus());
        return extractField(payment.getBody(), "payment_id");
    }

    private String extractField(String body, String fieldName) {
        String token = "\"" + fieldName + "\":\"";
        int start = body.indexOf(token);
        if (start < 0) {
            return "";
        }
        int valueStart = start + token.length();
        int end = body.indexOf("\"", valueStart);
        return end < 0 ? "" : body.substring(valueStart, end);
    }
}
