package com.ece493.cms.unit;

import com.ece493.cms.controller.StaticResourceServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StaticResourceServletTest {
    @Test
    void returnsCssWhenResourceExists() throws Exception {
        StaticResourceServlet servlet = new StaticResourceServlet("web/styles.css", "text/css");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(), response.asResponse());

        assertEquals(200, response.getStatus());
        assertEquals("text/css; charset=UTF-8", response.getContentType());
        assertTrue(response.getBody().contains(":root"));
    }

    @Test
    void returns404WhenResourceMissing() throws Exception {
        StaticResourceServlet servlet = new StaticResourceServlet("web/missing.css", "text/css");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(), response.asResponse());

        assertEquals(404, response.getStatus());
    }
}
