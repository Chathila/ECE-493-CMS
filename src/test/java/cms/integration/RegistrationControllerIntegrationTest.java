package cms.integration;

import cms.controllers.RegistrationController;
import cms.persistence.InMemoryUserRepository;
import cms.services.CmsPasswordPolicyService;
import cms.services.DefaultEmailValidationService;
import cms.services.Pbkdf2PasswordHasher;
import cms.services.RegistrationService;
import cms.services.RegistrationServiceImpl;
import cms.views.HtmlPageRenderer;

import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationControllerIntegrationTest {

    @Test
    void getRegisterRendersForm() throws Exception {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        RegistrationController controller = controller(repo);

        var response = ControllerTestSupport.handle(controller, ControllerTestSupport.get("/register"));

        assertEquals(200, response.getResponseCodeValue());
        assertTrue(response.getResponseText().contains("Register User Account"));
        assertTrue(response.getResponseText().contains("Email will be used as your username"));
    }

    @Test
    void postRegisterSuccessRedirectsToLoginAndPersists() throws Exception {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        RegistrationController controller = controller(repo);

        var response = ControllerTestSupport.handle(
            controller,
            ControllerTestSupport.post("/register", "email=valid%40example.com&password=Strong1%21")
        );

        assertEquals(303, response.getResponseCodeValue());
        assertEquals("/login?message=Registration+successful.+You+can+now+log+in.", response.getResponseHeaders().getFirst("Location"));
        assertEquals(1, repo.count());
    }

    @Test
    void postRegisterInvalidReturnsErrorAndNoCreate() throws Exception {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        RegistrationController controller = controller(repo);

        var response = ControllerTestSupport.handle(
            controller,
            ControllerTestSupport.post("/register", "email=not-an-email&password=Strong1%21")
        );

        assertEquals(400, response.getResponseCodeValue());
        assertTrue(response.getResponseText().contains("Please enter a valid email address."));
        assertEquals(0, repo.count());
    }

    @Test
    void unsupportedMethodReturns405() throws Exception {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        RegistrationController controller = controller(repo);

        var response = ControllerTestSupport.handle(controller, ControllerTestSupport.method("PUT", "/register", ""));

        assertEquals(405, response.getResponseCodeValue());
    }

    private RegistrationController controller(InMemoryUserRepository repo) {
        RegistrationService service = new RegistrationServiceImpl(
            repo,
            new DefaultEmailValidationService(),
            new CmsPasswordPolicyService(),
            new Pbkdf2PasswordHasher(),
            Clock.systemUTC()
        );
        return new RegistrationController(service, new HtmlPageRenderer());
    }
}
