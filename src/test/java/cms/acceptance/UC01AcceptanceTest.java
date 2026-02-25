package cms.acceptance;

import cms.app.CmsApplication;
import cms.integration.HttpTestSupport;
import cms.persistence.InMemoryUserRepository;
import cms.services.CmsPasswordPolicyService;
import cms.services.DefaultEmailValidationService;
import cms.services.Pbkdf2PasswordHasher;
import cms.services.RegistrationService;
import cms.services.RegistrationServiceImpl;

import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC01AcceptanceTest {

    @Test
    void at01_successfulRegistration() throws Exception {
        CmsApplication app = HttpTestSupport.startDefaultApp();
        try {
            HttpClient client = HttpTestSupport.clientWithoutRedirects();
            HttpResponse<String> response = HttpTestSupport.submitRegistration(client, app.baseUrl(), "newuser@example.com", "Strong1!");

            assertEquals(303, response.statusCode());
            assertEquals(1, app.getUserRepository().count());

            HttpResponse<String> loginPage = HttpTestSupport.get(client, app.baseUrl() + response.headers().firstValue("Location").orElse(""));
            assertTrue(loginPage.body().contains("Registration successful. You can now log in."));
        } finally {
            app.stop();
        }
    }

    @Test
    void at02_missingRequiredFields() throws Exception {
        CmsApplication app = HttpTestSupport.startDefaultApp();
        try {
            HttpClient client = HttpTestSupport.clientWithoutRedirects();
            HttpResponse<String> response = HttpTestSupport.submitRegistration(client, app.baseUrl(), "", "");

            assertEquals(400, response.statusCode());
            assertTrue(response.body().contains("Email and password are required."));
            assertEquals(0, app.getUserRepository().count());
        } finally {
            app.stop();
        }
    }

    @Test
    void at03_invalidEmailFormat() throws Exception {
        CmsApplication app = HttpTestSupport.startDefaultApp();
        try {
            HttpClient client = HttpTestSupport.clientWithoutRedirects();
            HttpResponse<String> response = HttpTestSupport.submitRegistration(client, app.baseUrl(), "invalid", "Strong1!");

            assertEquals(400, response.statusCode());
            assertTrue(response.body().contains("Please enter a valid email address."));
            assertEquals(0, app.getUserRepository().count());
        } finally {
            app.stop();
        }
    }

    @Test
    void at04_duplicateEmailAddress() throws Exception {
        CmsApplication app = HttpTestSupport.startDefaultApp();
        try {
            HttpClient client = HttpTestSupport.clientWithoutRedirects();
            HttpTestSupport.submitRegistration(client, app.baseUrl(), "dup@example.com", "Strong1!");
            HttpResponse<String> duplicate = HttpTestSupport.submitRegistration(client, app.baseUrl(), "dup@example.com", "Strong1!");

            assertEquals(400, duplicate.statusCode());
            assertTrue(duplicate.body().contains("already registered"));
            assertEquals(1, app.getUserRepository().count());
        } finally {
            app.stop();
        }
    }

    @Test
    void at05_weakPassword() throws Exception {
        CmsApplication app = HttpTestSupport.startDefaultApp();
        try {
            HttpClient client = HttpTestSupport.clientWithoutRedirects();
            HttpResponse<String> response = HttpTestSupport.submitRegistration(client, app.baseUrl(), "newmail@example.com", "weak");

            assertEquals(400, response.statusCode());
            assertTrue(response.body().contains("Password must be at least 8 characters"));
            assertEquals(0, app.getUserRepository().count());
        } finally {
            app.stop();
        }
    }

    @Test
    void at06_emailValidationServiceOutage() throws Exception {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        RegistrationService service = new RegistrationServiceImpl(
            repo,
            new DefaultEmailValidationService(() -> false),
            new CmsPasswordPolicyService(),
            new Pbkdf2PasswordHasher(),
            Clock.systemUTC()
        );
        CmsApplication app = CmsApplication.create(0, service, repo);
        app.start();

        try {
            HttpClient client = HttpTestSupport.clientWithoutRedirects();
            HttpResponse<String> response = HttpTestSupport.submitRegistration(client, app.baseUrl(), "ok@example.com", "Strong1!");

            assertEquals(400, response.statusCode());
            assertTrue(response.body().contains("Please try again later"));
            assertEquals(0, app.getUserRepository().count());
        } finally {
            app.stop();
        }
    }

    @Test
    void homeAndLoginEndpointsSupportGetAndRejectPost() throws Exception {
        CmsApplication app = HttpTestSupport.startDefaultApp();
        try {
            HttpClient client = HttpTestSupport.clientWithoutRedirects();

            HttpResponse<String> home = HttpTestSupport.get(client, app.baseUrl() + "/");
            assertEquals(200, home.statusCode());
            assertTrue(home.body().contains("/register"));

            HttpResponse<String> login = HttpTestSupport.get(client, app.baseUrl() + "/login");
            assertEquals(200, login.statusCode());

            HttpRequest postHome = HttpRequest.newBuilder(java.net.URI.create(app.baseUrl() + "/"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
            assertEquals(405, client.send(postHome, HttpResponse.BodyHandlers.ofString()).statusCode());

            HttpRequest postLogin = HttpRequest.newBuilder(java.net.URI.create(app.baseUrl() + "/login"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
            assertEquals(405, client.send(postLogin, HttpResponse.BodyHandlers.ofString()).statusCode());
        } finally {
            app.stop();
        }
    }
}
