package cms.app;

import cms.controllers.HomeController;
import cms.controllers.LoginController;
import cms.controllers.RegistrationController;
import cms.persistence.InMemoryUserRepository;
import cms.persistence.UserRepository;
import cms.services.CmsPasswordPolicyService;
import cms.services.DefaultEmailValidationService;
import cms.services.Pbkdf2PasswordHasher;
import cms.services.RegistrationService;
import cms.services.RegistrationServiceImpl;
import cms.views.HtmlPageRenderer;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Clock;
import java.util.concurrent.Executors;

public class CmsApplication {
    private final HttpServer server;
    private final UserRepository userRepository;

    private CmsApplication(HttpServer server, UserRepository userRepository) {
        this.server = server;
        this.userRepository = userRepository;
    }

    public static CmsApplication createDefault(int port) throws IOException {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        RegistrationService service = new RegistrationServiceImpl(
            repo,
            new DefaultEmailValidationService(),
            new CmsPasswordPolicyService(),
            new Pbkdf2PasswordHasher(),
            Clock.systemUTC()
        );
        return create(port, service, repo);
    }

    public static CmsApplication create(int port, RegistrationService registrationService, UserRepository userRepository) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());

        HtmlPageRenderer renderer = new HtmlPageRenderer();
        server.createContext("/", new HomeController(renderer));
        server.createContext("/register", new RegistrationController(registrationService, renderer));
        server.createContext("/login", new LoginController(renderer));

        return new CmsApplication(server, userRepository);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public int getPort() {
        return server.getAddress().getPort();
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public String baseUrl() {
        return "http://localhost:" + getPort();
    }

    public static void main(String[] args) throws Exception {
        int port = args.length == 0 ? 8080 : Integer.parseInt(args[0]);
        CmsApplication app = CmsApplication.createDefault(port);
        app.start();
        System.out.println("CMS running at " + app.baseUrl());
    }
}
