package com.ece493.cms.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StaticResourceServlet extends HttpServlet {
    private final String resourcePath;
    private final String contentType;

    public StaticResourceServlet(String resourcePath, String contentType) {
        this.resourcePath = resourcePath;
        this.contentType = contentType;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (stream == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        byte[] bytes = stream.readAllBytes();
        stream.close();

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(contentType + "; charset=UTF-8");
        resp.getWriter().write(new String(bytes, StandardCharsets.UTF_8));
    }
}
