package cms.controllers;

import cms.util.HttpUtil;
import cms.views.HtmlPageRenderer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class DashboardController implements HttpHandler {
    private final HtmlPageRenderer renderer;

    public DashboardController(HtmlPageRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        Map<String, String> params = HttpUtil.parseQuery(exchange.getRequestURI().getRawQuery());
        HttpUtil.sendHtml(exchange, 200, renderer.dashboardPage(params.get("role")));
    }
}
