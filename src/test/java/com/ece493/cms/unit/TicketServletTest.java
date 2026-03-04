package com.ece493.cms.unit;

import com.ece493.cms.controller.TicketServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.ConfirmationTicket;
import com.ece493.cms.model.TicketStatusResult;
import com.ece493.cms.model.TicketViewResult;
import com.ece493.cms.service.TicketService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketServletTest {
    @Test
    void handlesPostAndGetRoutes() throws Exception {
        TicketService service = new TicketService(null, null, null, null) {
            @Override
            public TicketStatusResult generateAndDeliver(String paymentId) {
                if ("p-ok".equals(paymentId)) {
                    return TicketStatusResult.delivered("t-1");
                }
                return TicketStatusResult.error(404, "Payment confirmation not found.");
            }

            @Override
            public TicketViewResult viewTicket(String ticketId) {
                if ("t-1".equals(ticketId)) {
                    return TicketViewResult.found(new ConfirmationTicket(
                            "t-1",
                            "attendee@cms.com",
                            "p-ok",
                            Instant.now(),
                            "ticket-content"
                    ));
                }
                return TicketViewResult.error(404, "Ticket not found.");
            }
        };
        TicketServlet servlet = new TicketServlet(service);

        ServletHttpTestSupport.ResponseCapture wrongPost = ServletHttpTestSupport.responseCapture();
        servlet.service(ServletHttpTestSupport.postJsonRequest("{}", null, "/payments/p-ok"), wrongPost.asResponse());
        assertEquals(404, wrongPost.getStatus());

        ServletHttpTestSupport.ResponseCapture okPost = ServletHttpTestSupport.responseCapture();
        servlet.service(ServletHttpTestSupport.postJsonRequest("{}", null, "/payments/p-ok/ticket"), okPost.asResponse());
        assertEquals(201, okPost.getStatus());
        assertTrue(okPost.getBody().contains("\"ticket_id\":\"t-1\""));

        ServletHttpTestSupport.ResponseCapture missingPost = ServletHttpTestSupport.responseCapture();
        servlet.service(ServletHttpTestSupport.postJsonRequest("{}", null, "/payments/p-miss/ticket"), missingPost.asResponse());
        assertEquals(404, missingPost.getStatus());

        ServletHttpTestSupport.ResponseCapture wrongGet = ServletHttpTestSupport.responseCapture();
        servlet.service(ServletHttpTestSupport.getRequest(null, null, "/ticket/t-1"), wrongGet.asResponse());
        assertEquals(404, wrongGet.getStatus());

        ServletHttpTestSupport.ResponseCapture okGet = ServletHttpTestSupport.responseCapture();
        servlet.service(ServletHttpTestSupport.getRequest(null, null, "/tickets/t-1"), okGet.asResponse());
        assertEquals(200, okGet.getStatus());
        assertTrue(okGet.getBody().contains("\"content\":\"ticket-content\""));

        ServletHttpTestSupport.ResponseCapture missGet = ServletHttpTestSupport.responseCapture();
        servlet.service(ServletHttpTestSupport.getRequest(null, null, "/tickets/t-404"), missGet.asResponse());
        assertEquals(404, missGet.getStatus());
    }

    @Test
    void parsePathReturnsNullForInvalidInput() throws Exception {
        TicketServlet servlet = new TicketServlet(new TicketService(null, null, null, null));
        Method parsePath = TicketServlet.class.getDeclaredMethod("parsePath", String.class, String.class);
        parsePath.setAccessible(true);
        assertNull(parsePath.invoke(servlet, null, "^/tickets/([^/]+)$"));
        assertNull(parsePath.invoke(servlet, "/unknown", "^/tickets/([^/]+)$"));
    }

    @Test
    void handlesNullMessagesInResponses() throws Exception {
        TicketServlet servlet = new TicketServlet(new TicketService(null, null, null, null) {
            @Override
            public TicketStatusResult generateAndDeliver(String paymentId) {
                return TicketStatusResult.error(500, null);
            }

            @Override
            public TicketViewResult viewTicket(String ticketId) {
                return TicketViewResult.error(500, null);
            }
        });

        ServletHttpTestSupport.ResponseCapture post = ServletHttpTestSupport.responseCapture();
        servlet.service(ServletHttpTestSupport.postJsonRequest("{}", null, "/payments/p/ticket"), post.asResponse());
        assertEquals(500, post.getStatus());
        assertTrue(post.getBody().contains("\"message\":\"\""));

        ServletHttpTestSupport.ResponseCapture get = ServletHttpTestSupport.responseCapture();
        servlet.service(ServletHttpTestSupport.getRequest(null, null, "/tickets/t"), get.asResponse());
        assertEquals(500, get.getStatus());
        assertTrue(get.getBody().contains("\"message\":\"\""));
    }
}
