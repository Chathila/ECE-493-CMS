package com.ece493.cms.integration;

import com.ece493.cms.controller.LoginServlet;
import com.ece493.cms.controller.PaperSubmissionServlet;
import com.ece493.cms.controller.RegistrationServlet;
import com.ece493.cms.controller.ChangePasswordServlet;
import com.ece493.cms.controller.DraftSaveServlet;
import com.ece493.cms.controller.DraftViewServlet;
import com.ece493.cms.db.Db;
import com.ece493.cms.model.UserAccount;
import com.ece493.cms.repository.JdbcPaperSubmissionDraftRepository;
import com.ece493.cms.repository.JdbcPaperSubmissionRepository;
import com.ece493.cms.repository.JdbcUserAccountRepository;
import com.ece493.cms.security.PasswordHasher;
import com.ece493.cms.service.AuthenticationService;
import com.ece493.cms.service.AuthenticationServiceImpl;
import com.ece493.cms.service.DefaultDraftValidationService;
import com.ece493.cms.service.DefaultAccountStatusService;
import com.ece493.cms.service.DefaultAuthenticationAvailabilityService;
import com.ece493.cms.service.DefaultEmailValidationService;
import com.ece493.cms.service.DefaultPasswordPolicyService;
import com.ece493.cms.service.DefaultMetadataValidationService;
import com.ece493.cms.service.DraftSaveService;
import com.ece493.cms.service.DraftSaveServiceImpl;
import com.ece493.cms.service.PasswordChangeService;
import com.ece493.cms.service.PasswordChangeServiceImpl;
import com.ece493.cms.service.PaperSubmissionService;
import com.ece493.cms.service.PaperSubmissionServiceImpl;
import com.ece493.cms.service.InMemoryFileStorageService;
import com.ece493.cms.service.RegistrationService;
import com.ece493.cms.service.RegistrationServiceImpl;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class RegistrationIntegrationSupport {
    protected DataSource dataSource;
    protected RegistrationServlet servlet;
    protected LoginServlet loginServlet;
    protected ChangePasswordServlet changePasswordServlet;
    protected PaperSubmissionServlet paperSubmissionServlet;
    protected DraftSaveServlet draftSaveServlet;
    protected DraftViewServlet draftViewServlet;
    protected InMemoryFileStorageService fileStorageService;

    protected void startApp() {
        dataSource = Db.createDataSource("jdbc:h2:mem:cms_it_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);

        RegistrationService registrationService = new RegistrationServiceImpl(
                new JdbcUserAccountRepository(dataSource),
                new DefaultEmailValidationService(),
                new DefaultPasswordPolicyService(),
                new PasswordHasher()
        );
        AuthenticationService authenticationService = new AuthenticationServiceImpl(
                new JdbcUserAccountRepository(dataSource),
                new PasswordHasher(),
                new DefaultAccountStatusService(),
                new DefaultAuthenticationAvailabilityService()
        );
        PasswordChangeService passwordChangeService = new PasswordChangeServiceImpl(
                new JdbcUserAccountRepository(dataSource),
                new DefaultPasswordPolicyService(),
                new PasswordHasher(),
                new DefaultAuthenticationAvailabilityService()
        );
        fileStorageService = new InMemoryFileStorageService();
        PaperSubmissionService paperSubmissionService = new PaperSubmissionServiceImpl(
                new JdbcPaperSubmissionRepository(dataSource),
                new DefaultMetadataValidationService(),
                fileStorageService
        );
        DraftSaveService draftSaveService = new DraftSaveServiceImpl(
                new JdbcPaperSubmissionDraftRepository(dataSource),
                new DefaultDraftValidationService()
        );

        servlet = new RegistrationServlet(registrationService, loadRegisterHtml());
        loginServlet = new LoginServlet(authenticationService, loadLoginHtml());
        changePasswordServlet = new ChangePasswordServlet(passwordChangeService, loadChangePasswordHtml());
        paperSubmissionServlet = new PaperSubmissionServlet(paperSubmissionService, loadSubmitPaperHtml());
        draftSaveServlet = new DraftSaveServlet(draftSaveService);
        draftViewServlet = new DraftViewServlet(new JdbcPaperSubmissionDraftRepository(dataSource));
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

    protected ServletHttpTestSupport.ResponseCapture postLogin(String payload) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        loginServlet.service(
                ServletHttpTestSupport.postJsonRequest(payload),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getLoginPage() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        loginServlet.service(
                ServletHttpTestSupport.getRequest(),
                response.asResponse()
        );
        return response;
    }

    protected void seedUser(String email, String rawPassword, String status, String role) {
        PasswordHasher hasher = new PasswordHasher();
        String salt = hasher.generateSalt();
        String hash = hasher.hashPassword(rawPassword, salt);
        new JdbcUserAccountRepository(dataSource).save(new UserAccount(
                0L,
                email,
                hash,
                salt,
                status,
                role,
                Instant.now()
        ));
    }

    protected ServletHttpTestSupport.SessionCapture loggedInSession(String email) {
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", email);
        return session;
    }

    protected ServletHttpTestSupport.ResponseCapture postChangePassword(String payload, ServletHttpTestSupport.SessionCapture session) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        changePasswordServlet.service(
                ServletHttpTestSupport.postJsonRequest(payload, session),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getChangePasswordPage() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        changePasswordServlet.service(
                ServletHttpTestSupport.getRequest(),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture postPaperSubmission(String payload, ServletHttpTestSupport.SessionCapture session) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        paperSubmissionServlet.service(
                ServletHttpTestSupport.postJsonRequest(payload, session),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture postDraftSave(String payload, ServletHttpTestSupport.SessionCapture session) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        draftSaveServlet.service(
                ServletHttpTestSupport.postJsonRequest(payload, session),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getDraft(ServletHttpTestSupport.SessionCapture session) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        draftViewServlet.service(
                ServletHttpTestSupport.getRequest(session),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getPaperSubmissionPage() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        paperSubmissionServlet.service(
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

    private String loadLoginHtml() {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("web/login.html")) {
            if (stream == null) {
                throw new IllegalStateException("Missing login view");
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load login view", e);
        }
    }

    private String loadChangePasswordHtml() {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("web/change-password.html")) {
            if (stream == null) {
                throw new IllegalStateException("Missing change-password view");
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load change-password view", e);
        }
    }

    private String loadSubmitPaperHtml() {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("web/submit-paper.html")) {
            if (stream == null) {
                throw new IllegalStateException("Missing submit-paper view");
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load submit-paper view", e);
        }
    }
}
