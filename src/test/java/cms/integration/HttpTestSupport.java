package cms.integration;

import cms.app.CmsApplication;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class HttpTestSupport {
    private HttpTestSupport() {
    }

    public static HttpClient clientWithoutRedirects() {
        return HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(2))
            .build();
    }

    public static HttpResponse<String> get(HttpClient client, String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> submitRegistration(HttpClient client, String baseUrl, String email, String password)
        throws IOException, InterruptedException {
        String body = "email=" + encode(email) + "&password=" + encode(password);
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/register"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static CmsApplication startDefaultApp() throws IOException {
        CmsApplication app = CmsApplication.createDefault(0);
        app.start();
        return app;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
