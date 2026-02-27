package com.ece493.cms.controller;

import com.ece493.cms.db.Db;
import com.ece493.cms.repository.JdbcUserAccountRepository;
import com.ece493.cms.repository.UserAccountRepository;
import com.ece493.cms.security.PasswordHasher;
import com.ece493.cms.service.AuthenticationService;
import com.ece493.cms.service.AuthenticationServiceImpl;
import com.ece493.cms.service.DefaultAccountStatusService;
import com.ece493.cms.service.DefaultAuthenticationAvailabilityService;
import com.ece493.cms.service.DefaultEmailValidationService;
import com.ece493.cms.service.DefaultPasswordPolicyService;
import com.ece493.cms.service.PasswordChangeService;
import com.ece493.cms.service.PasswordChangeServiceImpl;
import com.ece493.cms.service.RegistrationService;
import com.ece493.cms.service.RegistrationServiceImpl;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CmsAppServer {
    private final Server server;

    public CmsAppServer(int port, DataSource dataSource) {
        RegistrationService registrationService = createRegistrationService(dataSource);
        AuthenticationService authenticationService = createAuthenticationService(dataSource);
        PasswordChangeService passwordChangeService = createPasswordChangeService(dataSource);
        String registerHtml = loadRegisterHtml();
        String loginHtml = loadLoginHtml();
        String changePasswordHtml = loadChangePasswordHtml();

        this.server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new StaticResourceServlet("web/index.html", "text/html; charset=UTF-8")), "/");
        context.addServlet(new ServletHolder(new RegistrationServlet(registrationService, registerHtml)), "/register");
        context.addServlet(new ServletHolder(new LoginServlet(authenticationService, loginHtml)), "/login");
        context.addServlet(new ServletHolder(new ChangePasswordServlet(passwordChangeService, changePasswordHtml)), "/account/password");
        context.addServlet(new ServletHolder(new StaticResourceServlet("web/home.html", "text/html; charset=UTF-8")), "/home");
        context.addServlet(new ServletHolder(new StaticResourceServlet("web/home.html", "text/html; charset=UTF-8")), "/home/*");
        context.addServlet(new ServletHolder(new StaticResourceServlet("web/styles.css", "text/css")), "/styles.css");
        server.setHandler(context);
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public int port() {
        return server.getURI().getPort();
    }

    public static RegistrationService createRegistrationService(DataSource dataSource) {
        UserAccountRepository repository = new JdbcUserAccountRepository(dataSource);
        return new RegistrationServiceImpl(
                repository,
                new DefaultEmailValidationService(),
                new DefaultPasswordPolicyService(),
                new PasswordHasher()
        );
    }

    public static AuthenticationService createAuthenticationService(DataSource dataSource) {
        UserAccountRepository repository = new JdbcUserAccountRepository(dataSource);
        return new AuthenticationServiceImpl(
                repository,
                new PasswordHasher(),
                new DefaultAccountStatusService(),
                new DefaultAuthenticationAvailabilityService()
        );
    }

    public static PasswordChangeService createPasswordChangeService(DataSource dataSource) {
        UserAccountRepository repository = new JdbcUserAccountRepository(dataSource);
        return new PasswordChangeServiceImpl(
                repository,
                new DefaultPasswordPolicyService(),
                new PasswordHasher(),
                new DefaultAuthenticationAvailabilityService()
        );
    }

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getProperty("PORT", System.getenv().getOrDefault("PORT", "8080")));
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:cms;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);

        CmsAppServer appServer = new CmsAppServer(port, dataSource);
        appServer.start();
        appServer.server.join();
    }

    private String loadRegisterHtml() {
        return loadHtml("web/register.html", "register");
    }

    private String loadLoginHtml() {
        return loadHtml("web/login.html", "login");
    }

    private String loadChangePasswordHtml() {
        return loadHtml("web/change-password.html", "change-password");
    }

    private String loadHtml(String resourcePath, String viewName) {
        try (InputStream stream = Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath),
                "Missing " + viewName + " view"
        )) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + viewName + " view", e);
        }
    }
}
