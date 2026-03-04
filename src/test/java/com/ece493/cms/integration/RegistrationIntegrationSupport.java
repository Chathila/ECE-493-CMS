package com.ece493.cms.integration;

import com.ece493.cms.controller.LoginServlet;
import com.ece493.cms.controller.PaperSubmissionServlet;
import com.ece493.cms.controller.PaperSubmissionListServlet;
import com.ece493.cms.controller.PaperDetailsServlet;
import com.ece493.cms.controller.ReviewWorkflowServlet;
import com.ece493.cms.controller.ScheduleServlet;
import com.ece493.cms.controller.FinalScheduleServlet;
import com.ece493.cms.controller.RegistrationPaymentServlet;
import com.ece493.cms.controller.RegistrationPriceServlet;
import com.ece493.cms.controller.RegistrationServlet;
import com.ece493.cms.controller.ChangePasswordServlet;
import com.ece493.cms.controller.TicketServlet;
import com.ece493.cms.controller.DraftSaveServlet;
import com.ece493.cms.controller.DraftViewServlet;
import com.ece493.cms.controller.DraftListServlet;
import com.ece493.cms.controller.FileValidationServlet;
import com.ece493.cms.controller.InvitationResponseServlet;
import com.ece493.cms.controller.RefereeAssignmentServlet;
import com.ece493.cms.controller.ReviewDashboardServlet;
import com.ece493.cms.db.Db;
import com.ece493.cms.model.UserAccount;
import com.ece493.cms.repository.JdbcPaperSubmissionDraftRepository;
import com.ece493.cms.repository.JdbcPaperSubmissionRepository;
import com.ece493.cms.repository.JdbcRefereeAssignmentRepository;
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
import com.ece493.cms.service.FileValidationService;
import com.ece493.cms.service.FileValidationServiceImpl;
import com.ece493.cms.service.FinalDecisionNotificationService;
import com.ece493.cms.service.FinalDecisionService;
import com.ece493.cms.service.DeterministicSchedulingAlgorithm;
import com.ece493.cms.service.InMemoryFinalDecisionRepository;
import com.ece493.cms.service.InMemoryNotificationService;
import com.ece493.cms.service.InMemoryNotificationFailureRepository;
import com.ece493.cms.service.InMemoryPaymentRepository;
import com.ece493.cms.service.InMemoryPaymentService;
import com.ece493.cms.service.InMemoryRegistrationPriceRepository;
import com.ece493.cms.service.InMemoryScheduleRepository;
import com.ece493.cms.service.InMemorySchedulingDataRepository;
import com.ece493.cms.service.InMemorySessionRepository;
import com.ece493.cms.service.InMemoryTicketDeliveryService;
import com.ece493.cms.service.InMemoryTicketFailureRepository;
import com.ece493.cms.service.InMemoryTicketRepository;
import com.ece493.cms.service.InvitationResponseService;
import com.ece493.cms.service.PaymentProcessingService;
import com.ece493.cms.service.PaymentServiceDecision;
import com.ece493.cms.service.RegistrationService;
import com.ece493.cms.service.RegistrationServiceImpl;
import com.ece493.cms.service.RefereeAssignmentService;
import com.ece493.cms.service.RefereeAssignmentServiceImpl;
import com.ece493.cms.service.RegistrationPriceService;
import com.ece493.cms.service.ReviewAuthorizationService;
import com.ece493.cms.service.ReviewFormService;
import com.ece493.cms.service.ReviewSubmissionService;
import com.ece493.cms.service.InMemoryReviewAssignmentRepository;
import com.ece493.cms.service.InMemoryReviewFormRepository;
import com.ece493.cms.service.InMemoryReviewRepository;
import com.ece493.cms.service.DefaultReviewValidationService;
import com.ece493.cms.service.InMemoryEditorNotificationService;
import com.ece493.cms.service.InMemoryEmailDeliveryService;
import com.ece493.cms.service.ScheduleEditService;
import com.ece493.cms.service.ScheduleGenerationService;
import com.ece493.cms.service.ScheduleValidationService;
import com.ece493.cms.service.ScheduleViewService;
import com.ece493.cms.service.TicketService;

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
    protected PaperSubmissionListServlet paperSubmissionListServlet;
    protected PaperDetailsServlet paperDetailsServlet;
    protected DraftSaveServlet draftSaveServlet;
    protected DraftViewServlet draftViewServlet;
    protected DraftListServlet draftListServlet;
    protected FileValidationServlet fileValidationServlet;
    protected RefereeAssignmentServlet refereeAssignmentServlet;
    protected InvitationResponseServlet invitationResponseServlet;
    protected ReviewWorkflowServlet reviewWorkflowServlet;
    protected ReviewDashboardServlet reviewDashboardServlet;
    protected ScheduleServlet scheduleServlet;
    protected FinalScheduleServlet finalScheduleServlet;
    protected RegistrationPriceServlet registrationPriceServlet;
    protected RegistrationPaymentServlet registrationPaymentServlet;
    protected TicketServlet ticketServlet;
    protected InMemoryFileStorageService fileStorageService;
    protected InMemoryNotificationService notificationService;
    protected InMemoryReviewRepository reviewRepository;
    protected InMemoryFinalDecisionRepository finalDecisionRepository;
    protected InMemoryNotificationFailureRepository notificationFailureRepository;
    protected InMemoryEditorNotificationService finalDecisionEditorNotificationService;
    protected InMemoryEmailDeliveryService finalDecisionEmailDeliveryService;
    protected InMemoryScheduleRepository scheduleRepository;
    protected InMemorySessionRepository sessionRepository;
    protected InMemorySchedulingDataRepository schedulingDataRepository;
    protected DeterministicSchedulingAlgorithm schedulingAlgorithm;
    protected ScheduleGenerationService scheduleGenerationService;
    protected InMemoryRegistrationPriceRepository registrationPriceRepository;
    protected InMemoryPaymentRepository paymentRepository;
    protected InMemoryPaymentService paymentService;
    protected InMemoryTicketRepository ticketRepository;
    protected InMemoryTicketFailureRepository ticketFailureRepository;
    protected InMemoryTicketDeliveryService ticketDeliveryService;

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
                new JdbcPaperSubmissionDraftRepository(dataSource),
                new DefaultMetadataValidationService(),
                fileStorageService
        );
        DraftSaveService draftSaveService = new DraftSaveServiceImpl(
                new JdbcPaperSubmissionDraftRepository(dataSource),
                new DefaultDraftValidationService()
        );
        FileValidationService fileValidationService = new FileValidationServiceImpl(fileStorageService);
        notificationService = new InMemoryNotificationService();
        RefereeAssignmentService refereeAssignmentService = new RefereeAssignmentServiceImpl(
                new JdbcUserAccountRepository(dataSource),
                new JdbcRefereeAssignmentRepository(dataSource),
                notificationService
        );
        InvitationResponseService invitationResponseService = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );
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
        reviewRepository = new InMemoryReviewRepository();
        ReviewSubmissionService reviewSubmissionService = new ReviewSubmissionService(
                reviewAssignmentRepository,
                reviewRepository,
                new DefaultReviewValidationService(),
                reviewAuthorizationService,
                new InMemoryEditorNotificationService()
        );
        finalDecisionRepository = new InMemoryFinalDecisionRepository();
        notificationFailureRepository = new InMemoryNotificationFailureRepository();
        finalDecisionEditorNotificationService = new InMemoryEditorNotificationService();
        finalDecisionEmailDeliveryService = new InMemoryEmailDeliveryService();
        FinalDecisionService finalDecisionService = new FinalDecisionService(
                finalDecisionRepository,
                new JdbcPaperSubmissionRepository(dataSource),
                notificationService.invitationRepository(),
                notificationService.reviewAssignmentService(),
                new FinalDecisionNotificationService(
                        finalDecisionEmailDeliveryService,
                        notificationFailureRepository,
                        finalDecisionEditorNotificationService
                )
        );
        scheduleRepository = new InMemoryScheduleRepository();
        sessionRepository = new InMemorySessionRepository();
        schedulingDataRepository = new InMemorySchedulingDataRepository();
        schedulingAlgorithm = new DeterministicSchedulingAlgorithm();
        scheduleGenerationService = new ScheduleGenerationService(
                scheduleRepository,
                sessionRepository,
                schedulingDataRepository,
                schedulingAlgorithm
        );
        ScheduleEditService scheduleEditService = new ScheduleEditService(
                scheduleRepository,
                sessionRepository,
                new ScheduleValidationService()
        );
        ScheduleViewService scheduleViewService = new ScheduleViewService(scheduleRepository, sessionRepository);
        registrationPriceRepository = new InMemoryRegistrationPriceRepository();
        RegistrationPriceService registrationPriceService = new RegistrationPriceService(registrationPriceRepository);
        paymentRepository = new InMemoryPaymentRepository();
        paymentService = new InMemoryPaymentService();
        PaymentProcessingService paymentProcessingService = new PaymentProcessingService(paymentRepository, paymentService);
        ticketRepository = new InMemoryTicketRepository();
        ticketFailureRepository = new InMemoryTicketFailureRepository();
        ticketDeliveryService = new InMemoryTicketDeliveryService();
        TicketService ticketService = new TicketService(
                paymentRepository,
                ticketRepository,
                ticketFailureRepository,
                ticketDeliveryService
        );

        servlet = new RegistrationServlet(registrationService, loadRegisterHtml());
        loginServlet = new LoginServlet(authenticationService, loadLoginHtml());
        changePasswordServlet = new ChangePasswordServlet(passwordChangeService, loadChangePasswordHtml());
        paperSubmissionServlet = new PaperSubmissionServlet(paperSubmissionService, loadSubmitPaperHtml());
        paperSubmissionListServlet = new PaperSubmissionListServlet(new JdbcPaperSubmissionRepository(dataSource));
        paperDetailsServlet = new PaperDetailsServlet(new JdbcPaperSubmissionRepository(dataSource), fileStorageService);
        draftSaveServlet = new DraftSaveServlet(draftSaveService);
        draftViewServlet = new DraftViewServlet(new JdbcPaperSubmissionDraftRepository(dataSource));
        draftListServlet = new DraftListServlet(new JdbcPaperSubmissionDraftRepository(dataSource));
        fileValidationServlet = new FileValidationServlet(fileValidationService);
        refereeAssignmentServlet = new RefereeAssignmentServlet(refereeAssignmentService, finalDecisionService, loadAssignRefereesHtml());
        invitationResponseServlet = new InvitationResponseServlet(invitationResponseService);
        reviewWorkflowServlet = new ReviewWorkflowServlet(reviewFormService, reviewSubmissionService);
        reviewDashboardServlet = new ReviewDashboardServlet(
                notificationService.invitationRepository(),
                notificationService.reviewAssignmentService(),
                new JdbcPaperSubmissionRepository(dataSource)
        );
        scheduleServlet = new ScheduleServlet(scheduleGenerationService, scheduleEditService);
        finalScheduleServlet = new FinalScheduleServlet(scheduleViewService);
        registrationPriceServlet = new RegistrationPriceServlet(registrationPriceService);
        registrationPaymentServlet = new RegistrationPaymentServlet(paymentProcessingService);
        ticketServlet = new TicketServlet(ticketService);
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

    protected ServletHttpTestSupport.ResponseCapture getPaperSubmissions(ServletHttpTestSupport.SessionCapture session) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        paperSubmissionListServlet.service(
                ServletHttpTestSupport.getRequest(session),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getPaperDetails(ServletHttpTestSupport.SessionCapture session, String submissionId) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        paperDetailsServlet.service(
                ServletHttpTestSupport.getRequest(session, null, "/papers/details/" + submissionId),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getPaperFile(ServletHttpTestSupport.SessionCapture session, String fileId) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        paperDetailsServlet.service(
                ServletHttpTestSupport.getRequest(session, null, "/papers/files/" + fileId),
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

    protected ServletHttpTestSupport.ResponseCapture getDraft(ServletHttpTestSupport.SessionCapture session, String draftId) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        draftViewServlet.service(
                ServletHttpTestSupport.getRequest(session, java.util.Map.of("id", draftId)),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getDrafts(ServletHttpTestSupport.SessionCapture session) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        draftListServlet.service(
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

    protected ServletHttpTestSupport.ResponseCapture postFileValidation(String payload, ServletHttpTestSupport.SessionCapture session) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        fileValidationServlet.service(
                ServletHttpTestSupport.postJsonRequest(payload, session),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture postRefereeAssignment(
            String payload,
            ServletHttpTestSupport.SessionCapture session,
            String path
    ) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        refereeAssignmentServlet.service(
                ServletHttpTestSupport.postJsonRequest(payload, session, path),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getInvitation(String invitationId) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        invitationResponseServlet.service(
                ServletHttpTestSupport.getRequest(null, null, "/invitations/" + invitationId),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture postInvitationResponse(String invitationId, String payload) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        invitationResponseServlet.service(
                ServletHttpTestSupport.postJsonRequest(payload, null, "/invitations/" + invitationId + "/response"),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture postInvitationApproval(String invitationId) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        invitationResponseServlet.service(
                ServletHttpTestSupport.postJsonRequest("{}", null, "/invitations/" + invitationId + "/approval"),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getReviewDashboard(ServletHttpTestSupport.SessionCapture session) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        reviewDashboardServlet.service(
                ServletHttpTestSupport.getRequest(session, null, "/reviews/dashboard"),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getReviewForm(String assignmentId, ServletHttpTestSupport.SessionCapture session) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        reviewWorkflowServlet.service(
                ServletHttpTestSupport.getRequest(session, null, "/assignments/" + assignmentId + "/review-form"),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture postReviewSubmission(
            String assignmentId,
            String payload,
            ServletHttpTestSupport.SessionCapture session
    ) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        reviewWorkflowServlet.service(
                ServletHttpTestSupport.postJsonRequest(payload, session, "/assignments/" + assignmentId + "/reviews"),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture postFinalDecision(
            String paperId,
            String payload,
            ServletHttpTestSupport.SessionCapture session
    ) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        refereeAssignmentServlet.service(
                ServletHttpTestSupport.postJsonRequest(payload, session, "/papers/" + paperId + "/decision"),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture postFinalDecisionNotify(
            String paperId,
            ServletHttpTestSupport.SessionCapture session
    ) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        refereeAssignmentServlet.service(
                ServletHttpTestSupport.postJsonRequest("{}", session, "/papers/" + paperId + "/decision/notify"),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getFinalDecisionStatus(
            String paperId,
            ServletHttpTestSupport.SessionCapture session
    ) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        refereeAssignmentServlet.service(
                ServletHttpTestSupport.getRequest(session, null, "/papers/" + paperId + "/decision"),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture postScheduleGenerate(ServletHttpTestSupport.SessionCapture session) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        scheduleServlet.service(
                ServletHttpTestSupport.postJsonRequest("{}", session, "/schedule/generate"),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getSchedule(String scheduleId, ServletHttpTestSupport.SessionCapture session) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        scheduleServlet.service(
                ServletHttpTestSupport.getRequest(session, null, "/schedule/" + scheduleId),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture putSchedule(
            String scheduleId,
            String payload,
            ServletHttpTestSupport.SessionCapture session
    ) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        scheduleServlet.service(
                ServletHttpTestSupport.putJsonRequest(payload, session, "/schedule/" + scheduleId),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getFinalSchedule() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        finalScheduleServlet.service(
                ServletHttpTestSupport.getRequest(null, null, "/schedule/final"),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getRegistrationPrices() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        registrationPriceServlet.service(
                ServletHttpTestSupport.getRequest(null, null, "/registration/prices"),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture postRegistrationPayment(
            String payload,
            ServletHttpTestSupport.SessionCapture session
    ) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        registrationPaymentServlet.service(
                ServletHttpTestSupport.postJsonRequest(payload, session, "/registration/payments"),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture postGenerateTicket(String paymentId) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ticketServlet.service(
                ServletHttpTestSupport.postJsonRequest("{}", null, "/payments/" + paymentId + "/ticket"),
                response.asResponse()
        );
        return response;
    }

    protected ServletHttpTestSupport.ResponseCapture getTicket(String ticketId) throws Exception {
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ticketServlet.service(
                ServletHttpTestSupport.getRequest(null, null, "/tickets/" + ticketId),
                response.asResponse()
        );
        return response;
    }

    protected void setPaymentDecision(PaymentServiceDecision decision) {
        paymentService.setNextDecision(decision);
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

    private String loadAssignRefereesHtml() {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("web/assign-referees.html")) {
            if (stream == null) {
                throw new IllegalStateException("Missing assign-referees view");
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load assign-referees view", e);
        }
    }
}
