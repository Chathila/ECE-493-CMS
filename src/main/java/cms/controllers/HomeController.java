package cms.controllers;

import cms.util.HttpUtil;
import cms.views.HtmlPageRenderer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class HomeController implements HttpHandler {
    private final HtmlPageRenderer renderer;

    public HomeController(HtmlPageRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }
        HttpUtil.sendHtml(exchange, 200, renderer.homePage());
    }
}
