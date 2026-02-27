package com.ece493.cms.integration;

import com.ece493.cms.controller.RegistrationServlet;
import com.ece493.cms.db.Db;
import com.ece493.cms.repository.JdbcUserAccountRepository;
import com.ece493.cms.security.PasswordHasher;
import com.ece493.cms.service.DefaultEmailValidationService;
import com.ece493.cms.service.DefaultPasswordPolicyService;
import com.ece493.cms.service.RegistrationService;
import com.ece493.cms.service.RegistrationServiceImpl;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RegistrationIntegrationSupport {
    protected DataSource dataSource;
    protected RegistrationServlet servlet;

    protected void startApp() {
        dataSource = Db.createDataSource("jdbc:h2:mem:cms_it;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);

        RegistrationService registrationService = new RegistrationServiceImpl(
                new JdbcUserAccountRepository(dataSource),
                new DefaultEmailValidationService(),
                new DefaultPasswordPolicyService(),
                new PasswordHasher()
        );

        servlet = new RegistrationServlet(registrationService, loadRegisterHtml());
    }

    protected void stopApp() {
        // No-op for in-process servlet tests.
    }

    protected ServletHttpTestSupport.ResponseCapture postRegister(String payload) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        servlet.service(
                ServletHttpTestSupport.postJsonRequest(payload),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getRegisterPage() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        servlet.service(
                ServletHttpTestSupport.getRequest(),
                response.asResponse()
        );
        return response;
    }

    private String loadRegisterHtml() {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("web/register.html")) {
            if (stream == null) {
                throw new IllegalStateException("Missing register view");
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load register view", e);
        }
    }
}
