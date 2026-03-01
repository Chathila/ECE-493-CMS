package com.ece493.cms.unit;

import com.ece493.cms.controller.DraftListServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.PaperSubmissionDraft;
import com.ece493.cms.repository.PaperSubmissionDraftRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DraftListServletTest {
    @Test
    void returnsUnauthorizedWhenSessionMissing() throws Exception {
        DraftListServlet servlet = new DraftListServlet(repo(List.of()));
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(), response.asResponse());

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("log in"));
    }

    @Test
    void returnsUnauthorizedWhenSessionEmailBlank() throws Exception {
        DraftListServlet servlet = new DraftListServlet(repo(List.of()));
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", " ");

        servlet.service(ServletHttpTestSupport.getRequest(session), response.asResponse());

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("log in"));
    }

    @Test
    void returnsDraftListForLoggedInUser() throws Exception {
        DraftListServlet servlet = new DraftListServlet(repo(List.of(
                new PaperSubmissionDraft(2L, "author@cms.com", "Draft Two", "A", "U", "X", "k", "author@cms.com", Instant.now()),
                new PaperSubmissionDraft(1L, "author@cms.com", "Draft One", "A", "U", "X", "k", "author@cms.com", Instant.now())
        )));
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "author@cms.com");

        servlet.service(ServletHttpTestSupport.getRequest(session), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"draft_id\":2"));
        assertTrue(response.getBody().contains("Draft One"));
    }

    private PaperSubmissionDraftRepository repo(List<PaperSubmissionDraft> drafts) {
        return new PaperSubmissionDraftRepository() {
            @Override
            public Optional<PaperSubmissionDraft> findByIdAndAuthorEmail(long draftId, String authorEmail) {
                return Optional.empty();
            }

            @Override
            public List<PaperSubmissionDraft> findAllByAuthorEmail(String authorEmail) {
                return drafts;
            }

            @Override
            public long save(PaperSubmissionDraft draft) {
                return 0;
            }

            @Override
            public boolean update(PaperSubmissionDraft draft) {
                return false;
            }

            @Override
            public boolean deleteByIdAndAuthorEmail(long draftId, String authorEmail) {
                return false;
            }

            @Override
            public long countAll() {
                return drafts.size();
            }
        };
    }
}
