package cms.integration;

import cms.controllers.DashboardController;
import cms.controllers.LoginController;
import cms.models.AccountStatus;
import cms.models.UserAccount;
import cms.models.UserRole;
import cms.persistence.InMemoryUserRepository;
import cms.services.AuthenticationService;
import cms.services.AuthenticationServiceImpl;
import cms.services.Pbkdf2PasswordHasher;
import cms.views.HtmlPageRenderer;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginControllerIntegrationTest {

    @Test
    void getLoginRendersForm() throws Exception {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        LoginController controller = controller(repo, () -> true);

        var response = ControllerTestSupport.handle(controller, ControllerTestSupport.get("/login"));

        assertEquals(200, response.getResponseCodeValue());
        assertTrue(response.getResponseText().contains("<form method=\"post\" action=\"/login\">"));
    }

    @Test
    void postLoginWithValidCredentialsRedirectsToDashboard() throws Exception {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        var pair = new Pbkdf2PasswordHasher().hash("Strong1!");
        repo.save(new UserAccount("valid@example.com", pair.hash(), pair.salt(), AccountStatus.ACTIVE, UserRole.AUTHOR, Instant.now()));

        LoginController controller = controller(repo, () -> true);
        var response = ControllerTestSupport.handle(
            controller,
            ControllerTestSupport.post("/login", "email=valid%40example.com&password=Strong1%21")
        );

        assertEquals(303, response.getResponseCodeValue());
        assertEquals("/dashboard?role=author", response.getResponseHeaders().getFirst("Location"));
    }

    @Test
    void postLoginWithInvalidCredentialsReturns401() throws Exception {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        var pair = new Pbkdf2PasswordHasher().hash("Strong1!");
        repo.save(new UserAccount("valid@example.com", pair.hash(), pair.salt(), AccountStatus.ACTIVE, UserRole.AUTHOR, Instant.now()));
        LoginController controller = controller(repo, () -> true);

        var response = ControllerTestSupport.handle(
            controller,
            ControllerTestSupport.post("/login", "email=valid%40example.com&password=Wrong1%21")
        );

        assertEquals(401, response.getResponseCodeValue());
        assertTrue(response.getResponseText().contains("Invalid email or password."));
    }

    @Test
    void authenticationServiceOutageReturns503() throws Exception {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        LoginController controller = controller(repo, () -> false);

        var response = ControllerTestSupport.handle(
            controller,
            ControllerTestSupport.post("/login", "email=any%40example.com&password=Strong1%21")
        );

        assertEquals(503, response.getResponseCodeValue());
        assertTrue(response.getResponseText().contains("Authentication service is unavailable"));
    }

    @Test
    void dashboardSupportsGetAndRejectsPost() throws Exception {
        DashboardController dashboardController = new DashboardController(new HtmlPageRenderer());

        var getResponse = ControllerTestSupport.handle(
            dashboardController,
            ControllerTestSupport.get("/dashboard?role=author")
        );
        assertEquals(200, getResponse.getResponseCodeValue());
        assertTrue(getResponse.getResponseText().contains("Authorized Home Page"));

        var postResponse = ControllerTestSupport.handle(
            dashboardController,
            ControllerTestSupport.post("/dashboard", "")
        );
        assertEquals(405, postResponse.getResponseCodeValue());
    }

    @Test
    void unsupportedMethodOnLoginReturns405() throws Exception {
        LoginController controller = controller(new InMemoryUserRepository(), () -> true);

        var response = ControllerTestSupport.handle(
            controller,
            ControllerTestSupport.method("PUT", "/login", "")
        );

        assertEquals(405, response.getResponseCodeValue());
    }

    private LoginController controller(InMemoryUserRepository repo, java.util.function.BooleanSupplier availability) {
        AuthenticationService auth = new AuthenticationServiceImpl(repo, new Pbkdf2PasswordHasher(), availability);
        return new LoginController(auth, new HtmlPageRenderer());
    }
}
