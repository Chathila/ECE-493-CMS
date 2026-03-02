package com.ece493.cms.integration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

public final class ServletHttpTestSupport {
    private ServletHttpTestSupport() {
    }

    public static HttpServletRequest postJsonRequest(String jsonBody) {
        return postJsonRequest(jsonBody, null);
    }

    public static HttpServletRequest postJsonRequest(String jsonBody, SessionCapture sessionCapture) {
        return postJsonRequest(jsonBody, sessionCapture, null);
    }

    public static HttpServletRequest postJsonRequest(String jsonBody, SessionCapture sessionCapture, String requestUri) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                ServletHttpTestSupport.class.getClassLoader(),
                new Class[]{HttpServletRequest.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("getReader".equals(name)) {
                        return new BufferedReader(new StringReader(jsonBody));
                    }
                    if ("getMethod".equals(name)) {
                        return "POST";
                    }
                    if ("getRequestURI".equals(name)) {
                        return requestUri;
                    }
                    if ("getSession".equals(name)) {
                        boolean create = args != null && args.length == 1 && Boolean.TRUE.equals(args[0]);
                        if (sessionCapture == null) {
                            return create ? new SessionCapture().asSession() : null;
                        }
                        return sessionCapture.asSession();
                    }
                    return defaultValue(method.getReturnType());
                }
        );
    }

    public static HttpServletRequest putJsonRequest(String jsonBody) {
        return putJsonRequest(jsonBody, null);
    }

    public static HttpServletRequest putJsonRequest(String jsonBody, SessionCapture sessionCapture) {
        return putJsonRequest(jsonBody, sessionCapture, null);
    }

    public static HttpServletRequest putJsonRequest(String jsonBody, SessionCapture sessionCapture, String requestUri) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                ServletHttpTestSupport.class.getClassLoader(),
                new Class[]{HttpServletRequest.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("getReader".equals(name)) {
                        return new BufferedReader(new StringReader(jsonBody));
                    }
                    if ("getMethod".equals(name)) {
                        return "PUT";
                    }
                    if ("getRequestURI".equals(name)) {
                        return requestUri;
                    }
                    if ("getSession".equals(name)) {
                        boolean create = args != null && args.length == 1 && Boolean.TRUE.equals(args[0]);
                        if (sessionCapture == null) {
                            return create ? new SessionCapture().asSession() : null;
                        }
                        return sessionCapture.asSession();
                    }
                    return defaultValue(method.getReturnType());
                }
        );
    }

    public static HttpServletRequest getRequest() {
        return getRequest(null, null, null);
    }

    public static HttpServletRequest getRequest(SessionCapture sessionCapture) {
        return getRequest(sessionCapture, null, null);
    }

    public static HttpServletRequest getRequest(SessionCapture sessionCapture, Map<String, String> params) {
        return getRequest(sessionCapture, params, null);
    }

    public static HttpServletRequest getRequest(SessionCapture sessionCapture, Map<String, String> params, String requestUri) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                ServletHttpTestSupport.class.getClassLoader(),
                new Class[]{HttpServletRequest.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("getMethod".equals(name)) {
                        return "GET";
                    }
                    if ("getParameter".equals(name) && args != null && args.length == 1 && params != null) {
                        return params.get((String) args[0]);
                    }
                    if ("getRequestURI".equals(name)) {
                        return requestUri;
                    }
                    if ("getSession".equals(name)) {
                        boolean create = args != null && args.length == 1 && Boolean.TRUE.equals(args[0]);
                        if (sessionCapture == null) {
                            return create ? new SessionCapture().asSession() : null;
                        }
                        return sessionCapture.asSession();
                    }
                    return defaultValue(method.getReturnType());
                }
        );
    }

    public static ResponseCapture responseCapture() {
        return new ResponseCapture();
    }

    public static SessionCapture sessionCapture() {
        return new SessionCapture();
    }

    public static final class ResponseCapture {
        private final StringWriter bodyWriter = new StringWriter();
        private final ByteArrayOutputStream outputStreamBody = new ByteArrayOutputStream();
        private final PrintWriter printWriter = new PrintWriter(bodyWriter, true);
        private final Map<String, String> headers = new HashMap<>();
        private int status = 200;
        private String contentType;

        public HttpServletResponse asResponse() {
            return (HttpServletResponse) Proxy.newProxyInstance(
                    ServletHttpTestSupport.class.getClassLoader(),
                    new Class[]{HttpServletResponse.class},
                    (proxy, method, args) -> {
                        String name = method.getName();
                        if ("setStatus".equals(name)) {
                            status = (Integer) args[0];
                            return null;
                        }
                        if ("getStatus".equals(name)) {
                            return status;
                        }
                        if ("setHeader".equals(name)) {
                            headers.put((String) args[0], (String) args[1]);
                            return null;
                        }
                        if ("getHeader".equals(name)) {
                            return headers.get((String) args[0]);
                        }
                        if ("setContentType".equals(name)) {
                            contentType = (String) args[0];
                            return null;
                        }
                        if ("getContentType".equals(name)) {
                            return contentType;
                        }
                        if ("getWriter".equals(name)) {
                            return printWriter;
                        }
                        if ("getOutputStream".equals(name)) {
                            return new ServletOutputStream() {
                                @Override
                                public boolean isReady() {
                                    return true;
                                }

                                @Override
                                public void setWriteListener(WriteListener writeListener) {
                                }

                                @Override
                                public void write(int b) {
                                    outputStreamBody.write(b);
                                }
                            };
                        }
                        return defaultValue(method.getReturnType());
                    }
            );
        }

        public int getStatus() {
            return status;
        }

        public String getBody() {
            printWriter.flush();
            if (outputStreamBody.size() > 0) {
                return outputStreamBody.toString(StandardCharsets.UTF_8);
            }
            return bodyWriter.toString();
        }

        public String getHeader(String key) {
            return headers.get(key);
        }

        public String getContentType() {
            return contentType;
        }
    }

    public static final class SessionCapture {
        private final Map<String, Object> attributes = new HashMap<>();
        private boolean invalidated;

        public HttpSession asSession() {
            return (HttpSession) Proxy.newProxyInstance(
                    ServletHttpTestSupport.class.getClassLoader(),
                    new Class[]{HttpSession.class},
                    (proxy, method, args) -> {
                        String name = method.getName();
                        if ("getAttribute".equals(name)) {
                            return attributes.get((String) args[0]);
                        }
                        if ("setAttribute".equals(name)) {
                            attributes.put((String) args[0], args[1]);
                            return null;
                        }
                        if ("invalidate".equals(name)) {
                            invalidated = true;
                            attributes.clear();
                            return null;
                        }
                        if ("isNew".equals(name)) {
                            return false;
                        }
                        return defaultValue(method.getReturnType());
                    }
            );
        }

        public Object getAttribute(String key) {
            return attributes.get(key);
        }

        public boolean isInvalidated() {
            return invalidated;
        }
    }

    private static Object defaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }
        if (returnType == boolean.class) {
            return false;
        }
        if (returnType == byte.class) {
            return (byte) 0;
        }
        if (returnType == short.class) {
            return (short) 0;
        }
        if (returnType == int.class) {
            return 0;
        }
        if (returnType == long.class) {
            return 0L;
        }
        if (returnType == float.class) {
            return 0f;
        }
        if (returnType == double.class) {
            return 0d;
        }
        if (returnType == char.class) {
            return '\0';
        }
        return null;
    }
}
