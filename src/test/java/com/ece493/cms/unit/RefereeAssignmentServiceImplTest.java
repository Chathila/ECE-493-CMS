package com.ece493.cms.unit;

import com.ece493.cms.model.RefereeAssignmentResult;
import com.ece493.cms.model.UserAccount;
import com.ece493.cms.repository.RefereeAssignmentRepository;
import com.ece493.cms.repository.UserAccountRepository;
import com.ece493.cms.service.NotificationService;
import com.ece493.cms.service.RefereeAssignmentServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefereeAssignmentServiceImplTest {
    @Test
    void rejectsWhenEditorNotLoggedIn() {
        StubUserRepo userRepo = new StubUserRepo();
        StubAssignmentRepo assignmentRepo = new StubAssignmentRepo();
        RefereeAssignmentServiceImpl service = service(userRepo, assignmentRepo, new StubNotificationService());

        RefereeAssignmentResult result = service.assignReferees("", "1", List.of("ref@cms.com"));

        assertEquals(401, result.getStatusCode());
        assertEquals(0, assignmentRepo.saved.size());
    }

    @Test
    void rejectsWhenEditorEmailIsNull() {
        StubUserRepo userRepo = new StubUserRepo();
        StubAssignmentRepo assignmentRepo = new StubAssignmentRepo();
        RefereeAssignmentServiceImpl service = service(userRepo, assignmentRepo, new StubNotificationService());

        RefereeAssignmentResult result = service.assignReferees(null, "1", List.of("ref@cms.com"));

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void rejectsWhenPaperIdMissing() {
        StubUserRepo userRepo = new StubUserRepo();
        StubAssignmentRepo assignmentRepo = new StubAssignmentRepo();
        RefereeAssignmentServiceImpl service = service(userRepo, assignmentRepo, new StubNotificationService());

        RefereeAssignmentResult result = service.assignReferees("editor@cms.com", " ", List.of("ref@cms.com"));

        assertEquals(400, result.getStatusCode());
        assertTrue(result.getMessage().contains("Paper id"));
    }

    @Test
    void rejectsWhenNoRefereesProvided() {
        StubUserRepo userRepo = new StubUserRepo();
        StubAssignmentRepo assignmentRepo = new StubAssignmentRepo();
        RefereeAssignmentServiceImpl service = service(userRepo, assignmentRepo, new StubNotificationService());

        RefereeAssignmentResult result = service.assignReferees("editor@cms.com", "1", java.util.Arrays.asList(" ", null));

        assertEquals(400, result.getStatusCode());
        assertTrue(result.getMessage().contains("At least one"));
    }

    @Test
    void rejectsWhenRefereeEmailListIsNull() {
        StubUserRepo userRepo = new StubUserRepo();
        StubAssignmentRepo assignmentRepo = new StubAssignmentRepo();
        RefereeAssignmentServiceImpl service = service(userRepo, assignmentRepo, new StubNotificationService());

        RefereeAssignmentResult result = service.assignReferees("editor@cms.com", "1", null);

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void rejectsInvalidRefereeEmail() {
        StubUserRepo userRepo = new StubUserRepo();
        userRepo.addUser("valid@cms.com");
        StubAssignmentRepo assignmentRepo = new StubAssignmentRepo();
        RefereeAssignmentServiceImpl service = service(userRepo, assignmentRepo, new StubNotificationService());

        RefereeAssignmentResult result = service.assignReferees("editor@cms.com", "1", List.of("missing@cms.com"));

        assertEquals(400, result.getStatusCode());
        assertTrue(result.getMessage().contains("Invalid referee email"));
        assertEquals(0, assignmentRepo.saved.size());
    }

    @Test
    void rejectsWhenWorkloadLimitExceeded() {
        StubUserRepo userRepo = new StubUserRepo();
        userRepo.addUser("ref@cms.com");
        StubAssignmentRepo assignmentRepo = new StubAssignmentRepo();
        assignmentRepo.countByEmail.put("ref@cms.com", RefereeAssignmentServiceImpl.DEFAULT_REFEREE_WORKLOAD_LIMIT);
        RefereeAssignmentServiceImpl service = service(userRepo, assignmentRepo, new StubNotificationService());

        RefereeAssignmentResult result = service.assignReferees("editor@cms.com", "1", List.of("ref@cms.com"));

        assertEquals(409, result.getStatusCode());
        assertTrue(result.getMessage().contains("workload limit"));
    }

    @Test
    void rejectsWhenWorkloadCheckFails() {
        StubUserRepo userRepo = new StubUserRepo();
        userRepo.addUser("ref@cms.com");
        StubAssignmentRepo assignmentRepo = new StubAssignmentRepo();
        assignmentRepo.throwOnCount = true;
        RefereeAssignmentServiceImpl service = service(userRepo, assignmentRepo, new StubNotificationService());

        RefereeAssignmentResult result = service.assignReferees("editor@cms.com", "1", List.of("ref@cms.com"));

        assertEquals(500, result.getStatusCode());
        assertTrue(result.getMessage().contains("workload check"));
    }

    @Test
    void savesAssignmentsAndSendsInvitationsOnSuccess() {
        StubUserRepo userRepo = new StubUserRepo();
        userRepo.addUser("ref1@cms.com");
        userRepo.addUser("ref2@cms.com");
        StubAssignmentRepo assignmentRepo = new StubAssignmentRepo();
        StubNotificationService notificationService = new StubNotificationService();
        RefereeAssignmentServiceImpl service = service(userRepo, assignmentRepo, notificationService);

        RefereeAssignmentResult result = service.assignReferees("editor@cms.com", "99", List.of("ref1@cms.com", "ref2@cms.com"));

        assertEquals(200, result.getStatusCode());
        assertEquals(2, assignmentRepo.saved.size());
        assertEquals(2, notificationService.lastEmails.size());
    }

    @Test
    void storesAssignmentsAndReturnsWarningWhenNotificationFails() {
        StubUserRepo userRepo = new StubUserRepo();
        userRepo.addUser("ref@cms.com");
        StubAssignmentRepo assignmentRepo = new StubAssignmentRepo();
        StubNotificationService notificationService = new StubNotificationService();
        notificationService.throwOnSend = true;
        RefereeAssignmentServiceImpl service = service(userRepo, assignmentRepo, notificationService);

        RefereeAssignmentResult result = service.assignReferees("editor@cms.com", "1", List.of("ref@cms.com"));

        assertEquals(503, result.getStatusCode());
        assertEquals(1, assignmentRepo.saved.size());
        assertTrue(result.getWarning().contains("Invitations were not sent"));
    }

    @Test
    void deduplicatesAndTrimsRefereeEmails() {
        StubUserRepo userRepo = new StubUserRepo();
        userRepo.addUser("ref@cms.com");
        StubAssignmentRepo assignmentRepo = new StubAssignmentRepo();
        StubNotificationService notificationService = new StubNotificationService();
        RefereeAssignmentServiceImpl service = service(userRepo, assignmentRepo, notificationService);

        RefereeAssignmentResult result = service.assignReferees(
                "editor@cms.com",
                "1",
                List.of(" ref@cms.com ", "ref@cms.com")
        );

        assertEquals(200, result.getStatusCode());
        assertEquals(1, assignmentRepo.saved.size());
    }

    @Test
    void usesDefaultWorkloadLimitWhenPolicyIsMissing() {
        assertEquals(
                RefereeAssignmentServiceImpl.DEFAULT_REFEREE_WORKLOAD_LIMIT,
                RefereeAssignmentServiceImpl.resolveConfiguredWorkloadLimit(null)
        );
        assertEquals(
                RefereeAssignmentServiceImpl.DEFAULT_REFEREE_WORKLOAD_LIMIT,
                RefereeAssignmentServiceImpl.resolveConfiguredWorkloadLimit(" ")
        );
    }

    @Test
    void usesConfiguredWorkloadLimitWhenPolicyIsValid() {
        assertEquals(7L, RefereeAssignmentServiceImpl.resolveConfiguredWorkloadLimit("7"));
        assertEquals(8L, RefereeAssignmentServiceImpl.resolveConfiguredWorkloadLimit(" 8 "));
    }

    @Test
    void fallsBackToDefaultWhenConfiguredWorkloadLimitIsInvalid() {
        assertEquals(
                RefereeAssignmentServiceImpl.DEFAULT_REFEREE_WORKLOAD_LIMIT,
                RefereeAssignmentServiceImpl.resolveConfiguredWorkloadLimit("0")
        );
        assertEquals(
                RefereeAssignmentServiceImpl.DEFAULT_REFEREE_WORKLOAD_LIMIT,
                RefereeAssignmentServiceImpl.resolveConfiguredWorkloadLimit("-3")
        );
        assertEquals(
                RefereeAssignmentServiceImpl.DEFAULT_REFEREE_WORKLOAD_LIMIT,
                RefereeAssignmentServiceImpl.resolveConfiguredWorkloadLimit("abc")
        );
    }

    private RefereeAssignmentServiceImpl service(
            StubUserRepo userRepo,
            StubAssignmentRepo assignmentRepo,
            StubNotificationService notificationService
    ) {
        return new RefereeAssignmentServiceImpl(userRepo, assignmentRepo, notificationService);
    }

    private static class StubUserRepo implements UserAccountRepository {
        private final Map<String, UserAccount> usersByEmail = new HashMap<>();

        @Override
        public boolean existsByEmail(String email) {
            return usersByEmail.containsKey(email);
        }

        @Override
        public Optional<UserAccount> findByEmail(String email) {
            return Optional.ofNullable(usersByEmail.get(email));
        }

        @Override
        public void save(UserAccount userAccount) {
            usersByEmail.put(userAccount.getEmail(), userAccount);
        }

        @Override
        public boolean updatePasswordCredentialsByEmail(String email, String passwordHash, String passwordSalt) {
            return false;
        }

        @Override
        public long countByEmail(String email) {
            return usersByEmail.containsKey(email) ? 1 : 0;
        }

        private void addUser(String email) {
            usersByEmail.put(email, new UserAccount(1L, email, "h", "s", "ACTIVE", "REFEREE", Instant.now()));
        }
    }

    private static class StubAssignmentRepo implements RefereeAssignmentRepository {
        private final List<String> saved = new java.util.ArrayList<>();
        private final Map<String, Long> countByEmail = new HashMap<>();
        private boolean throwOnCount;

        @Override
        public void saveAssignments(String paperId, List<String> refereeEmails) {
            for (String email : refereeEmails) {
                saved.add(paperId + ":" + email);
            }
        }

        @Override
        public long countAssignmentsByRefereeEmail(String refereeEmail) {
            if (throwOnCount) {
                throw new IllegalStateException("down");
            }
            return countByEmail.getOrDefault(refereeEmail, 0L);
        }

        @Override
        public long countAssignmentsByPaperId(String paperId) {
            return saved.stream().filter(entry -> entry.startsWith(paperId + ":")).count();
        }
    }

    private static class StubNotificationService implements NotificationService {
        private boolean throwOnSend;
        private List<String> lastEmails = List.of();

        @Override
        public void sendReviewInvitations(String paperId, List<String> refereeEmails) {
            if (throwOnSend) {
                throw new IllegalStateException("down");
            }
            lastEmails = refereeEmails;
        }
    }
}
