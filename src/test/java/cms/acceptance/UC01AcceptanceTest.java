package cms.acceptance;

import cms.controllers.HomeController;
import cms.controllers.LoginController;
import cms.controllers.RegistrationController;
import cms.models.RegistrationResult;
import cms.persistence.InMemoryUserRepository;
import cms.services.AuthenticationServiceImpl;
import cms.services.CmsPasswordPolicyService;
import cms.services.DefaultEmailValidationService;
import cms.services.Pbkdf2PasswordHasher;
import cms.services.RegistrationService;
import cms.services.RegistrationServiceImpl;
import cms.views.HtmlPageRenderer;
import cms.integration.ControllerTestSupport;

import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC01AcceptanceTest {

    @Test
    void at01_successfulRegistration() throws Exception {
        var context = context(true);

        var response = ControllerTestSupport.handle(
            context.registrationController,
            ControllerTestSupport.post("/register", "email=newuser%40example.com&password=Strong1%21")
        );

        assertEquals(303, response.getResponseCodeValue());
        assertEquals(1, context.repo.count());

        var login = ControllerTestSupport.handle(
            context.loginController,
            ControllerTestSupport.get(response.getResponseHeaders().getFirst("Location"))
        );
        assertTrue(login.getResponseText().contains("Registration successful. You can now log in."));
    }

    @Test
    void at02_missingRequiredFields() throws Exception {
        var context = context(true);

        var response = ControllerTestSupport.handle(
            context.registrationController,
            ControllerTestSupport.post("/register", "email=&password=")
        );

        assertEquals(400, response.getResponseCodeValue());
        assertTrue(response.getResponseText().contains("Email and password are required."));
        assertEquals(0, context.repo.count());
    }

    @Test
    void at03_invalidEmailFormat() throws Exception {
        var context = context(true);

        var response = ControllerTestSupport.handle(
            context.registrationController,
            ControllerTestSupport.post("/register", "email=invalid&password=Strong1%21")
        );

        assertEquals(400, response.getResponseCodeValue());
        assertTrue(response.getResponseText().contains("Please enter a valid email address."));
        assertEquals(0, context.repo.count());
    }

    @Test
    void at04_duplicateEmailAddress() throws Exception {
        var context = context(true);

        ControllerTestSupport.handle(
            context.registrationController,
            ControllerTestSupport.post("/register", "email=dup%40example.com&password=Strong1%21")
        );
        var duplicate = ControllerTestSupport.handle(
            context.registrationController,
            ControllerTestSupport.post("/register", "email=dup%40example.com&password=Strong1%21")
        );

        assertEquals(400, duplicate.getResponseCodeValue());
        assertTrue(duplicate.getResponseText().contains("already registered"));
        assertEquals(1, context.repo.count());
    }

    @Test
    void at05_weakPassword() throws Exception {
        var context = context(true);

        var response = ControllerTestSupport.handle(
            context.registrationController,
            ControllerTestSupport.post("/register", "email=newmail%40example.com&password=weak")
        );

        assertEquals(400, response.getResponseCodeValue());
        assertTrue(response.getResponseText().contains("Password must be at least 8 characters"));
        assertEquals(0, context.repo.count());
    }

    @Test
    void at06_emailValidationServiceOutage() throws Exception {
        var context = context(false);

        var response = ControllerTestSupport.handle(
            context.registrationController,
            ControllerTestSupport.post("/register", "email=ok%40example.com&password=Strong1%21")
        );

        assertEquals(400, response.getResponseCodeValue());
        assertTrue(response.getResponseText().contains("Please try again later"));
        assertEquals(0, context.repo.count());
    }

    @Test
    void homeAndLoginEndpointsSupportGetAndRejectPost() throws Exception {
        var context = context(true);

        var home = ControllerTestSupport.handle(context.homeController, ControllerTestSupport.get("/"));
        assertEquals(200, home.getResponseCodeValue());
        assertTrue(home.getResponseText().contains("/register"));

        var login = ControllerTestSupport.handle(context.loginController, ControllerTestSupport.get("/login"));
        assertEquals(200, login.getResponseCodeValue());

        var postHome = ControllerTestSupport.handle(context.homeController, ControllerTestSupport.post("/", ""));
        assertEquals(405, postHome.getResponseCodeValue());

        var postLogin = ControllerTestSupport.handle(context.loginController, ControllerTestSupport.post("/login", ""));
        assertEquals(401, postLogin.getResponseCodeValue());
    }

    private TestContext context(boolean emailServiceAvailable) {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        RegistrationService registrationService = new RegistrationServiceImpl(
            repo,
            new DefaultEmailValidationService(() -> emailServiceAvailable),
            new CmsPasswordPolicyService(),
            new Pbkdf2PasswordHasher(),
            Clock.systemUTC()
        );

        return new TestContext(
            repo,
            new RegistrationController(registrationService, new HtmlPageRenderer()),
            new LoginController(new AuthenticationServiceImpl(repo, new Pbkdf2PasswordHasher()), new HtmlPageRenderer()),
            new HomeController(new HtmlPageRenderer())
        );
    }

    private record TestContext(
        InMemoryUserRepository repo,
        RegistrationController registrationController,
        LoginController loginController,
        HomeController homeController
    ) {
    }
}
