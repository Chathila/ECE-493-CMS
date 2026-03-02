package com.ece493.cms.unit;

import com.ece493.cms.controller.RegistrationPriceServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.RegistrationPrice;
import com.ece493.cms.model.RegistrationPriceViewResult;
import com.ece493.cms.service.RegistrationPriceService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationPriceServletTest {
    @Test
    void rendersPricesAndErrorPayloads() throws Exception {
        RegistrationPriceServlet okServlet = new RegistrationPriceServlet(new RegistrationPriceService(null) {
            @Override
            public RegistrationPriceViewResult viewPrices() {
                return RegistrationPriceViewResult.found(List.of(new RegistrationPrice(1L, "student", 100.0)));
            }
        });
        ServletHttpTestSupport.ResponseCapture ok = ServletHttpTestSupport.responseCapture();
        okServlet.service(ServletHttpTestSupport.getRequest(null, null, "/registration/prices"), ok.asResponse());

        assertEquals(200, ok.getStatus());
        assertTrue(ok.getBody().contains("\"category\":\"student\""));

        RegistrationPriceServlet errorServlet = new RegistrationPriceServlet(new RegistrationPriceService(null) {
            @Override
            public RegistrationPriceViewResult viewPrices() {
                return RegistrationPriceViewResult.error(404, null);
            }
        });
        ServletHttpTestSupport.ResponseCapture error = ServletHttpTestSupport.responseCapture();
        errorServlet.service(ServletHttpTestSupport.getRequest(null, null, "/registration/prices"), error.asResponse());
        assertEquals(404, error.getStatus());
        assertTrue(error.getBody().contains("\"message\":\"\""));
    }
}
