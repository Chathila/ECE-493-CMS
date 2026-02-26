package cms.acceptance;

import cms.controllers.LoginController;
import cms.integration.ControllerTestSupport;
import cms.models.AccountStatus;
import cms.models.UserAccount;
import cms.models.UserRole;
import cms.persistence.InMemoryUserRepository;
import cms.services.AuthenticationServiceImpl;
import cms.services.Pbkdf2PasswordHasher;
import cms.views.HtmlPageRenderer;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC02AcceptanceTest {

    @Test
    void at01_successfulLoginWithValidCredentials() throws Exception {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        var pair = new Pbkdf2PasswordHasher().hash("Strong1!");
        repo.save(new UserAccount("reviewer@example.com", pair.hash(), pair.salt(), AccountStatus.ACTIVE, UserRole.AUTHOR, Instant.now()));

        LoginController controller = new LoginController(new AuthenticationServiceImpl(repo, new Pbkdf2PasswordHasher()), new HtmlPageRenderer());
        var response = ControllerTestSupport.handle(
            controller,
            ControllerTestSupport.post("/login", "email=reviewer%40example.com&password=Strong1%21")
        );

        assertEquals(303, response.getResponseCodeValue());
        assertEquals("/dashboard?role=author", response.getResponseHeaders().getFirst("Location"));
    }

    @Test
    void at02_missingRequiredFields() throws Exception {
        LoginController controller = new LoginController(
            new AuthenticationServiceImpl(new InMemoryUserRepository(), new Pbkdf2PasswordHasher()),
            new HtmlPageRenderer()
        );

        var response = ControllerTestSupport.handle(controller, ControllerTestSupport.post("/login", "email=&password="));

        assertEquals(401, response.getResponseCodeValue());
        assertTrue(response.getResponseText().contains("Email and password are required."));
    }

    @Test
    void at03_incorrectEmailOrPassword() throws Exception {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        var pair = new Pbkdf2PasswordHasher().hash("Strong1!");
        repo.save(new UserAccount("user@example.com", pair.hash(), pair.salt(), AccountStatus.ACTIVE, UserRole.AUTHOR, Instant.now()));

        LoginController controller = new LoginController(new AuthenticationServiceImpl(repo, new Pbkdf2PasswordHasher()), new HtmlPageRenderer());
        var response = ControllerTestSupport.handle(
            controller,
            ControllerTestSupport.post("/login", "email=user%40example.com&password=Wrong1%21")
        );

        assertEquals(401, response.getResponseCodeValue());
        assertTrue(response.getResponseText().contains("Invalid email or password."));
    }

    @Test
    void at04_inactiveOrLockedAccountDenied() throws Exception {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        var pair = new Pbkdf2PasswordHasher().hash("Strong1!");
        repo.save(new UserAccount("inactive@example.com", pair.hash(), pair.salt(), AccountStatus.INACTIVE, UserRole.AUTHOR, Instant.now()));

        LoginController controller = new LoginController(new AuthenticationServiceImpl(repo, new Pbkdf2PasswordHasher()), new HtmlPageRenderer());
        var response = ControllerTestSupport.handle(
            controller,
            ControllerTestSupport.post("/login", "email=inactive%40example.com&password=Strong1%21")
        );

        assertEquals(401, response.getResponseCodeValue());
        assertTrue(response.getResponseText().contains("inactive or locked"));
    }
}
