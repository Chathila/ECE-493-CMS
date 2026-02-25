package cms.controllers;

import cms.models.RegistrationRequest;
import cms.models.RegistrationResult;
import cms.services.RegistrationService;
import cms.util.HttpUtil;
import cms.views.HtmlPageRenderer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class RegistrationController implements HttpHandler {
    private final RegistrationService registrationService;
    private final HtmlPageRenderer renderer;

    public RegistrationController(RegistrationService registrationService, HtmlPageRenderer renderer) {
        this.registrationService = registrationService;
        this.renderer = renderer;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            HttpUtil.sendHtml(exchange, 200, renderer.registrationPage(null));
            return;
        }

        if (!"POST".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String body = HttpUtil.readRequestBody(exchange);
        Map<String, String> form = HttpUtil.parseForm(body);

        RegistrationRequest request = new RegistrationRequest(form.get("email"), form.get("password"));
        RegistrationResult result = registrationService.register(request);

        if (result.isSuccess()) {
            HttpUtil.redirect(exchange, result.getRedirectPath());
            return;
        }

        HttpUtil.sendHtml(exchange, 400, renderer.registrationPage(result.getErrors()));
    }
}
