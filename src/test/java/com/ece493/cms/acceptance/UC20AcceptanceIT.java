package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.service.PaymentServiceDecision;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC20AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_ticketGeneratedStoredAndDeliveredSuccessfully() throws Exception {
        String paymentId = createSuccessfulPayment();

        ServletHttpTestSupport.ResponseCapture response = postGenerateTicket(paymentId);

        assertEquals(201, response.getStatus());
        assertTrue(response.getBody().contains("\"status\":\"delivered\""));
        assertEquals(1, ticketRepository.count());
    }

    @Test
    void AT02_ticketDeliveryFailure() throws Exception {
        String paymentId = createSuccessfulPayment();
        ticketDeliveryService.setFailEmail(true);

        ServletHttpTestSupport.ResponseCapture response = postGenerateTicket(paymentId);

        assertEquals(500, response.getStatus());
        assertTrue(response.getBody().contains("available in the CMS"));
        assertEquals(1, ticketFailureRepository.findByTicketId(extractTicketId(response.getBody())).size());
    }

    @Test
    void AT03_attendeeAccessesTicketBeforeDelivery() throws Exception {
        String paymentId = createSuccessfulPayment();
        ticketDeliveryService.setFailEmail(true);
        ticketDeliveryService.setFailInSystem(true);
        ServletHttpTestSupport.ResponseCapture generate = postGenerateTicket(paymentId);
        String ticketId = extractTicketId(generate.getBody());

        ServletHttpTestSupport.ResponseCapture response = getTicket(ticketId);

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"ticket_id\":\"" + ticketId + "\""));
        assertTrue(response.getBody().contains("Payment ID"));
    }

    private String createSuccessfulPayment() throws Exception {
        setPaymentDecision(PaymentServiceDecision.APPROVED);
        ServletHttpTestSupport.ResponseCapture payment = postRegistrationPayment(
                "{\"registration_type\":\"author\",\"payment_details\":{\"method\":\"card\"}}",
                loggedInSession("attendee@cms.com")
        );
        assertEquals(200, payment.getStatus());
        return extractPaymentId(payment.getBody());
    }

    private String extractPaymentId(String body) {
        return extractField(body, "payment_id");
    }

    private String extractTicketId(String body) {
        return extractField(body, "ticket_id");
    }

    private String extractField(String body, String name) {
        String token = "\"" + name + "\":\"";
        int start = body.indexOf(token);
        if (start < 0) {
            return "";
        }
        int valueStart = start + token.length();
        int end = body.indexOf("\"", valueStart);
        return end < 0 ? "" : body.substring(valueStart, end);
    }
}
