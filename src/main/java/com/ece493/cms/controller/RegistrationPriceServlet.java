package com.ece493.cms.controller;

import com.ece493.cms.model.RegistrationPrice;
import com.ece493.cms.model.RegistrationPriceViewResult;
import com.ece493.cms.service.RegistrationPriceService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class RegistrationPriceServlet extends HttpServlet {
    private final RegistrationPriceService registrationPriceService;

    public RegistrationPriceServlet(RegistrationPriceService registrationPriceService) {
        this.registrationPriceService = registrationPriceService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RegistrationPriceViewResult result = registrationPriceService.viewPrices();
        resp.setStatus(result.getStatusCode());
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{\"message\":\"" + escapeJson(nonNull(result.getMessage())) + "\"";
        if (!result.getPrices().isEmpty()) {
            payload += ",\"prices\":" + pricesJson(result.getPrices());
        }
        payload += "}";
        resp.getWriter().write(payload);
    }

    private String pricesJson(List<RegistrationPrice> prices) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < prices.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            RegistrationPrice price = prices.get(i);
            json.append("{")
                    .append("\"category\":\"").append(escapeJson(nonNull(price.getCategory()))).append("\",")
                    .append("\"amount\":").append(price.getAmount())
                    .append("}");
        }
        json.append("]");
        return json.toString();
    }

    private String nonNull(String value) {
        return value == null ? "" : value;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
