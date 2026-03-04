package com.ece493.cms.unit;

import com.ece493.cms.controller.RegistrationPaymentServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.PaymentProcessingResult;
import com.ece493.cms.service.PaymentProcessingService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationPaymentServletTest {
    @Test
    void enforcesRoutingAndLoginAndRendersResult() throws Exception {
        RegistrationPaymentServlet servlet = new RegistrationPaymentServlet(new PaymentProcessingService(null, null) {
            @Override
            public PaymentProcessingResult process(String attendeeEmail, String registrationType, String paymentDetailsPayload) {
                return PaymentProcessingResult.success("42");
            }
        });

        ServletHttpTestSupport.ResponseCapture wrongRoute = ServletHttpTestSupport.responseCapture();
        servlet.service(
                ServletHttpTestSupport.postJsonRequest("{}", null, "/registration/payment"),
                wrongRoute.asResponse()
        );
        assertEquals(404, wrongRoute.getStatus());

        ServletHttpTestSupport.ResponseCapture unauthenticated = ServletHttpTestSupport.responseCapture();
        servlet.service(
                ServletHttpTestSupport.postJsonRequest("{}", null, "/registration/payments"),
                unauthenticated.asResponse()
        );
        assertEquals(401, unauthenticated.getStatus());

        ServletHttpTestSupport.ResponseCapture blankUser = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture blankSession = ServletHttpTestSupport.sessionCapture();
        blankSession.asSession().setAttribute("user_email", "   ");
        servlet.service(
                ServletHttpTestSupport.postJsonRequest("{}", blankSession, "/registration/payments"),
                blankUser.asResponse()
        );
        assertEquals(401, blankUser.getStatus());

        ServletHttpTestSupport.ResponseCapture ok = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "attendee@cms.com");
        servlet.service(
                ServletHttpTestSupport.postJsonRequest(
                        "{\"registration_type\":\"student\",\"payment_details\":{\"method\":\"card\"}}",
                        session,
                        "/registration/payments"
                ),
                ok.asResponse()
        );
        assertEquals(200, ok.getStatus());
        assertTrue(ok.getBody().contains("\"payment_id\":\"42\""));
        assertTrue(ok.getBody().contains("\"status\":\"confirmed\""));
    }

    @Test
    void parserHelpersHandleNullAndMissingPayloads() throws Exception {
        RegistrationPaymentServlet servlet = new RegistrationPaymentServlet(new PaymentProcessingService(null, null));
        Method parseTextField = RegistrationPaymentServlet.class.getDeclaredMethod("parseTextField", String.class, String.class);
        Method parseObject = RegistrationPaymentServlet.class.getDeclaredMethod("parseObject", String.class, String.class);
        parseTextField.setAccessible(true);
        parseObject.setAccessible(true);

        assertNull(parseTextField.invoke(servlet, null, "registration_type"));
        assertNull(parseTextField.invoke(servlet, "{\"x\":\"y\"}", "registration_type"));
        assertNull(parseObject.invoke(servlet, null, "payment_details"));
        assertNull(parseObject.invoke(servlet, "{\"x\":{}}", "payment_details"));
    }

    @Test
    void rendersEmptyMessageWhenResultMessageIsNull() throws Exception {
        Constructor<PaymentProcessingResult> ctor = PaymentProcessingResult.class
                .getDeclaredConstructor(int.class, String.class, String.class, String.class);
        ctor.setAccessible(true);
        PaymentProcessingResult nullMessage = ctor.newInstance(400, null, null, null);
        RegistrationPaymentServlet servlet = new RegistrationPaymentServlet(new PaymentProcessingService(null, null) {
            @Override
            public PaymentProcessingResult process(String attendeeEmail, String registrationType, String paymentDetailsPayload) {
                return nullMessage;
            }
        });
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "attendee@cms.com");
        servlet.service(
                ServletHttpTestSupport.postJsonRequest(
                        "{\"registration_type\":\"student\",\"payment_details\":{\"method\":\"card\"}}",
                        session,
                        "/registration/payments"
                ),
                response.asResponse()
        );
        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("\"message\":\"\""));
    }
}
