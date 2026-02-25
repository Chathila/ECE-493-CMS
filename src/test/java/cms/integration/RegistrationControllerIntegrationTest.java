package cms.integration;

import cms.app.CmsApplication;

import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationControllerIntegrationTest {

    @Test
    void getRegisterRendersForm() throws Exception {
        CmsApplication app = HttpTestSupport.startDefaultApp();
        try {
            HttpClient client = HttpTestSupport.clientWithoutRedirects();
            HttpResponse<String> response = HttpTestSupport.get(client, app.baseUrl() + "/register");

            assertEquals(200, response.statusCode());
            assertTrue(response.body().contains("Register User Account"));
            assertTrue(response.body().contains("Email will be used as your username"));
        } finally {
            app.stop();
        }
    }

    @Test
    void postRegisterSuccessRedirectsToLoginAndPersists() throws Exception {
        CmsApplication app = HttpTestSupport.startDefaultApp();
        try {
            HttpClient client = HttpTestSupport.clientWithoutRedirects();
            HttpResponse<String> response = HttpTestSupport.submitRegistration(
                client,
                app.baseUrl(),
                "valid@example.com",
                "Strong1!"
            );

            assertEquals(303, response.statusCode());
            assertEquals("/login?message=Registration+successful.+You+can+now+log+in.", response.headers().firstValue("Location").orElse(""));
            assertEquals(1, app.getUserRepository().count());
        } finally {
            app.stop();
        }
    }

    @Test
    void postRegisterInvalidReturnsErrorAndNoCreate() throws Exception {
        CmsApplication app = HttpTestSupport.startDefaultApp();
        try {
            HttpClient client = HttpTestSupport.clientWithoutRedirects();
            HttpResponse<String> response = HttpTestSupport.submitRegistration(
                client,
                app.baseUrl(),
                "not-an-email",
                "Strong1!"
            );

            assertEquals(400, response.statusCode());
            assertTrue(response.body().contains("Please enter a valid email address."));
            assertEquals(0, app.getUserRepository().count());
        } finally {
            app.stop();
        }
    }

    @Test
    void unsupportedMethodReturns405() throws Exception {
        CmsApplication app = HttpTestSupport.startDefaultApp();
        try {
            HttpClient client = HttpTestSupport.clientWithoutRedirects();
            var request = java.net.http.HttpRequest.newBuilder(java.net.URI.create(app.baseUrl() + "/register"))
                .method("PUT", java.net.http.HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(405, response.statusCode());
        } finally {
            app.stop();
        }
    }
}
