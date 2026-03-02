package com.ece493.cms.unit;

import com.ece493.cms.model.ReviewerWorkload;
import com.ece493.cms.repository.RefereeAssignmentRepository;
import com.ece493.cms.service.DefaultWorkloadCheckService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultWorkloadCheckServiceTest {
    @Test
    void returnsReviewerWorkloadWhenCountIsAvailable() {
        StubAssignmentRepo repo = new StubAssignmentRepo();
        repo.countValue = 3L;
        DefaultWorkloadCheckService service = new DefaultWorkloadCheckService(repo, 5L);

        ReviewerWorkload workload = service.getReviewerWorkload("ref@cms.com");

        assertEquals("ref@cms.com", workload.getReviewerEmail());
        assertEquals(3L, workload.getAssignedCount());
        assertEquals(5L, workload.getWorkloadLimit());
        assertTrue(workload.canAssign());
    }

    @Test
    void rejectsMissingReviewerEmail() {
        DefaultWorkloadCheckService service = new DefaultWorkloadCheckService(new StubAssignmentRepo(), 5L);

        assertThrows(IllegalStateException.class, () -> service.getReviewerWorkload(" "));
        assertThrows(IllegalStateException.class, () -> service.getReviewerWorkload(null));
    }

    @Test
    void wrapsRepositoryFailuresAsWorkloadUnavailable() {
        StubAssignmentRepo repo = new StubAssignmentRepo();
        repo.throwOnCount = true;
        DefaultWorkloadCheckService service = new DefaultWorkloadCheckService(repo, 5L);

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> service.getReviewerWorkload("ref@cms.com"));

        assertTrue(error.getMessage().contains("unavailable"));
    }

    @Test
    void rejectsNegativeWorkloadCounts() {
        StubAssignmentRepo repo = new StubAssignmentRepo();
        repo.countValue = -1L;
        DefaultWorkloadCheckService service = new DefaultWorkloadCheckService(repo, 5L);

        assertThrows(IllegalStateException.class, () -> service.getReviewerWorkload("ref@cms.com"));
    }

    private static class StubAssignmentRepo implements RefereeAssignmentRepository {
        private long countValue;
        private boolean throwOnCount;

        @Override
        public void saveAssignments(String paperId, List<String> refereeEmails) {
        }

        @Override
        public long countAssignmentsByRefereeEmail(String refereeEmail) {
            if (throwOnCount) {
                throw new IllegalStateException("down");
            }
            return countValue;
        }

        @Override
        public long countAssignmentsByPaperId(String paperId) {
            return 0;
        }
    }
}
