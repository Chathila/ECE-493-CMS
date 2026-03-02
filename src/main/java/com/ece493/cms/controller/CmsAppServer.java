package com.ece493.cms.controller;

import com.ece493.cms.db.Db;
import com.ece493.cms.repository.JdbcUserAccountRepository;
import com.ece493.cms.repository.JdbcPaperSubmissionRepository;
import com.ece493.cms.repository.JdbcPaperSubmissionDraftRepository;
import com.ece493.cms.repository.JdbcRefereeAssignmentRepository;
import com.ece493.cms.repository.PaperSubmissionRepository;
import com.ece493.cms.repository.PaperSubmissionDraftRepository;
import com.ece493.cms.repository.RefereeAssignmentRepository;
import com.ece493.cms.repository.UserAccountRepository;
import com.ece493.cms.security.PasswordHasher;
import com.ece493.cms.service.AuthenticationService;
import com.ece493.cms.service.AuthenticationServiceImpl;
import com.ece493.cms.service.DefaultDraftValidationService;
import com.ece493.cms.service.DefaultAccountStatusService;
import com.ece493.cms.service.DefaultAuthenticationAvailabilityService;
import com.ece493.cms.service.DefaultEmailValidationService;
import com.ece493.cms.service.DefaultMetadataValidationService;
import com.ece493.cms.service.DefaultPasswordPolicyService;
import com.ece493.cms.service.DraftSaveService;
import com.ece493.cms.service.DraftSaveServiceImpl;
import com.ece493.cms.service.FileValidationService;
import com.ece493.cms.service.FileValidationServiceImpl;
import com.ece493.cms.service.InMemoryFileStorageService;
import com.ece493.cms.service.InMemoryNotificationService;
import com.ece493.cms.service.InvitationResponseService;
import com.ece493.cms.service.NotificationService;
import com.ece493.cms.service.PasswordChangeService;
import com.ece493.cms.service.PasswordChangeServiceImpl;
import com.ece493.cms.service.PaperSubmissionService;
import com.ece493.cms.service.PaperSubmissionServiceImpl;
import com.ece493.cms.service.RegistrationService;
import com.ece493.cms.service.RegistrationServiceImpl;
import com.ece493.cms.service.RefereeAssignmentService;
import com.ece493.cms.service.RefereeAssignmentServiceImpl;
import com.ece493.cms.service.ReviewAuthorizationService;
import com.ece493.cms.service.ReviewFormService;
import com.ece493.cms.service.ReviewSubmissionService;
import com.ece493.cms.service.InMemoryReviewAssignmentRepository;
import com.ece493.cms.service.InMemoryReviewFormRepository;
import com.ece493.cms.service.InMemoryReviewRepository;
import com.ece493.cms.service.DefaultReviewValidationService;
import com.ece493.cms.service.InMemoryEditorNotificationService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

public class CmsAppServer {
    private final Server server;

    public CmsAppServer(int port, DataSource dataSource) {
        RegistrationService registrationService = createRegistrationService(dataSource);
        AuthenticationService authenticationService = createAuthenticationService(dataSource);
        PasswordChangeService passwordChangeService = createPasswordChangeService(dataSource);
        InMemoryFileStorageService fileStorageService = new InMemoryFileStorageService();
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        PaperSubmissionService paperSubmissionService = createPaperSubmissionService(dataSource, fileStorageService);
        FileValidationService fileValidationService = createFileValidationService(fileStorageService);
        RefereeAssignmentService refereeAssignmentService = createRefereeAssignmentService(dataSource, notificationService);
        InvitationResponseService invitationResponseService = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );
        DraftSaveService draftSaveService = createDraftSaveService(dataSource);
        InMemoryReviewAssignmentRepository reviewAssignmentRepository = new InMemoryReviewAssignmentRepository(
                notificationService.invitationRepository(),
                notificationService.reviewAssignmentService()
        );
        ReviewAuthorizationService reviewAuthorizationService = new ReviewAuthorizationService();
        ReviewFormService reviewFormService = new ReviewFormService(
                reviewAssignmentRepository,
                new InMemoryReviewFormRepository(),
                new JdbcPaperSubmissionRepository(dataSource),
                reviewAuthorizationService
        );
        ReviewSubmissionService reviewSubmissionService = new ReviewSubmissionService(
                reviewAssignmentRepository,
                new InMemoryReviewRepository(),
                new DefaultReviewValidationService(),
                reviewAuthorizationService,
                new InMemoryEditorNotificationService()
        );
        String registerHtml = loadRegisterHtml();
        String loginHtml = loadLoginHtml();
        String changePasswordHtml = loadChangePasswordHtml();
        String submitPaperHtml = loadSubmitPaperHtml();
        String assignRefereesHtml = loadAssignRefereesHtml();

        this.server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new StaticResourceServlet("web/index.html", "text/html; charset=UTF-8")), "/");
        context.addServlet(new ServletHolder(new RegistrationServlet(registrationService, registerHtml)), "/register");
        context.addServlet(new ServletHolder(new LoginServlet(authenticationService, loginHtml)), "/login");
        context.addServlet(new ServletHolder(new ChangePasswordServlet(passwordChangeService, changePasswordHtml)), "/account/password");
        context.addServlet(new ServletHolder(new PaperSubmissionServlet(paperSubmissionService, submitPaperHtml)), "/papers/submit");
        context.addServlet(new ServletHolder(new PaperSubmissionListServlet(new JdbcPaperSubmissionRepository(dataSource))), "/papers/submissions");
        context.addServlet(new ServletHolder(new FileValidationServlet(fileValidationService)), "/papers/file/validate");
        context.addServlet(new ServletHolder(new DraftSaveServlet(draftSaveService)), "/papers/draft/save");
        context.addServlet(new ServletHolder(new DraftViewServlet(new JdbcPaperSubmissionDraftRepository(dataSource))), "/papers/draft");
        context.addServlet(new ServletHolder(new DraftListServlet(new JdbcPaperSubmissionDraftRepository(dataSource))), "/papers/drafts");
        PaperDetailsServlet paperDetailsServlet = new PaperDetailsServlet(new JdbcPaperSubmissionRepository(dataSource), fileStorageService);
        context.addServlet(new ServletHolder(paperDetailsServlet), "/papers/details/*");
        context.addServlet(new ServletHolder(paperDetailsServlet), "/papers/files/*");
        context.addServlet(new ServletHolder(new RefereeAssignmentServlet(refereeAssignmentService, assignRefereesHtml)), "/papers/*");
        context.addServlet(new ServletHolder(new InvitationResponseServlet(invitationResponseService)), "/invitations/*");
        context.addServlet(new ServletHolder(new ReviewWorkflowServlet(reviewFormService, reviewSubmissionService)), "/assignments/*");
        context.addServlet(new ServletHolder(new ReviewDashboardServlet(
                notificationService.invitationRepository(),
                notificationService.reviewAssignmentService(),
                new JdbcPaperSubmissionRepository(dataSource)
        )), "/reviews/dashboard");
        context.addServlet(new ServletHolder(new StaticResourceServlet("web/review-form.html", "text/html; charset=UTF-8")), "/review-form");
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

    public static PaperSubmissionService createPaperSubmissionService(DataSource dataSource, InMemoryFileStorageService fileStorageService) {
        PaperSubmissionRepository repository = new JdbcPaperSubmissionRepository(dataSource);
        PaperSubmissionDraftRepository draftRepository = new JdbcPaperSubmissionDraftRepository(dataSource);
        return new PaperSubmissionServiceImpl(
                repository,
                draftRepository,
                new DefaultMetadataValidationService(),
                fileStorageService
        );
    }

    public static DraftSaveService createDraftSaveService(DataSource dataSource) {
        PaperSubmissionDraftRepository repository = new JdbcPaperSubmissionDraftRepository(dataSource);
        return new DraftSaveServiceImpl(repository, new DefaultDraftValidationService());
    }

    public static FileValidationService createFileValidationService(InMemoryFileStorageService fileStorageService) {
        return new FileValidationServiceImpl(fileStorageService);
    }

    public static RefereeAssignmentService createRefereeAssignmentService(DataSource dataSource, NotificationService notificationService) {
        UserAccountRepository userAccountRepository = new JdbcUserAccountRepository(dataSource);
        RefereeAssignmentRepository refereeAssignmentRepository = new JdbcRefereeAssignmentRepository(dataSource);
        return new RefereeAssignmentServiceImpl(
                userAccountRepository,
                refereeAssignmentRepository,
                notificationService
        );
    }

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getProperty("PORT", System.getenv().getOrDefault("PORT", "8080")));
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:cms;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        seedDefaultTestUser(dataSource);

        CmsAppServer appServer = new CmsAppServer(port, dataSource);
        appServer.start();
        appServer.server.join();
    }

    private static void seedDefaultTestUser(DataSource dataSource) {
        UserAccountRepository repository = new JdbcUserAccountRepository(dataSource);
        String defaultEmail = "admin@user.com";
        if (repository.existsByEmail(defaultEmail)) {
            return;
        }

        PasswordHasher hasher = new PasswordHasher();
        String salt = hasher.generateSalt();
        String hash = hasher.hashPassword("monkey", salt);
        repository.save(new com.ece493.cms.model.UserAccount(
                0L,
                defaultEmail,
                hash,
                salt,
                "ACTIVE",
                "AUTHOR",
                "Admin User",
                Instant.now()
        ));
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

    private String loadSubmitPaperHtml() {
        return loadHtml("web/submit-paper.html", "submit-paper");
    }

    private String loadAssignRefereesHtml() {
        return loadHtml("web/assign-referees.html", "assign-referees");
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
