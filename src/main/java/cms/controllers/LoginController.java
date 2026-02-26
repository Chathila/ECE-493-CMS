package cms.controllers;

import cms.models.LoginRequest;
import cms.models.LoginResult;
import cms.services.AuthenticationMessages;
import cms.services.AuthenticationService;
import cms.services.AuthenticationServiceUnavailableException;
import cms.util.HttpUtil;
import cms.views.HtmlPageRenderer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class LoginController implements HttpHandler {
    private final AuthenticationService authenticationService;
    private final HtmlPageRenderer renderer;

    public LoginController(AuthenticationService authenticationService, HtmlPageRenderer renderer) {
        this.authenticationService = authenticationService;
        this.renderer = renderer;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            Map<String, String> queryParams = HttpUtil.parseQuery(exchange.getRequestURI().getRawQuery());
            HttpUtil.sendHtml(exchange, 200, renderer.loginPage(queryParams.get("message"), null));
            return;
        }

        if (!"POST".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String body = HttpUtil.readRequestBody(exchange);
        Map<String, String> form = HttpUtil.parseForm(body);

        LoginResult result;
        try {
            result = authenticationService.login(new LoginRequest(form.get("email"), form.get("password")));
        } catch (AuthenticationServiceUnavailableException ex) {
            HttpUtil.sendHtml(exchange, 503, renderer.loginPage(null, java.util.List.of(AuthenticationMessages.TRY_AGAIN_LATER)));
            return;
        }

        if (result.isSuccess()) {
            HttpUtil.redirect(exchange, result.getRedirectPath());
            return;
        }

        HttpUtil.sendHtml(exchange, 401, renderer.loginPage(null, result.getErrors()));
    }
}
