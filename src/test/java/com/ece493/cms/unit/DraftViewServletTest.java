package com.ece493.cms.unit;

import com.ece493.cms.controller.DraftViewServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.PaperSubmissionDraft;
import com.ece493.cms.repository.PaperSubmissionDraftRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DraftViewServletTest {
    @Test
    void returnsUnauthorizedWhenSessionMissing() throws Exception {
        DraftViewServlet servlet = new DraftViewServlet(emptyRepo());
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(), response.asResponse());

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("log in"));
    }

    @Test
    void returnsNotFoundWhenDraftMissing() throws Exception {
        DraftViewServlet servlet = new DraftViewServlet(emptyRepo());
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "author@cms.com");

        servlet.service(ServletHttpTestSupport.getRequest(session), response.asResponse());

        assertEquals(404, response.getStatus());
        assertTrue(response.getBody().contains("No draft"));
    }

    @Test
    void returnsUnauthorizedWhenSessionEmailBlank() throws Exception {
        DraftViewServlet servlet = new DraftViewServlet(emptyRepo());
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", " ");

        servlet.service(ServletHttpTestSupport.getRequest(session), response.asResponse());

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("log in"));
    }

    @Test
    void returnsDraftWhenPresent() throws Exception {
        DraftViewServlet servlet = new DraftViewServlet(singleDraftRepo());
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "author@cms.com");

        servlet.service(ServletHttpTestSupport.getRequest(session), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"title\":\"Draft Title\""));
        assertTrue(response.getBody().contains("\"contact_details\":\"author@cms.com\""));
    }

    @Test
    void returnsDraftWithNullOptionalFieldsAsEmptyStrings() throws Exception {
        DraftViewServlet servlet = new DraftViewServlet(new PaperSubmissionDraftRepository() {
            @Override
            public Optional<PaperSubmissionDraft> findByAuthorEmail(String authorEmail) {
                return Optional.of(new PaperSubmissionDraft(
                        1L, "author@cms.com", "Draft Title", null, null, null, null, "author@cms.com", Instant.now()
                ));
            }

            @Override
            public void saveOrUpdate(PaperSubmissionDraft draft) {
            }

            @Override
            public long countAll() {
                return 1;
            }
        });
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "author@cms.com");

        servlet.service(ServletHttpTestSupport.getRequest(session), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"authors\":\"\""));
        assertTrue(response.getBody().contains("\"affiliations\":\"\""));
    }

    private PaperSubmissionDraftRepository emptyRepo() {
        return new PaperSubmissionDraftRepository() {
            @Override
            public Optional<PaperSubmissionDraft> findByAuthorEmail(String authorEmail) {
                return Optional.empty();
            }

            @Override
            public void saveOrUpdate(PaperSubmissionDraft draft) {
            }

            @Override
            public long countAll() {
                return 0;
            }
        };
    }

    private PaperSubmissionDraftRepository singleDraftRepo() {
        return new PaperSubmissionDraftRepository() {
            @Override
            public Optional<PaperSubmissionDraft> findByAuthorEmail(String authorEmail) {
                return Optional.of(new PaperSubmissionDraft(
                        1L,
                        "author@cms.com",
                        "Draft Title",
                        "Alice",
                        "U1",
                        "Abstract",
                        "k1",
                        "author@cms.com",
                        Instant.now()
                ));
            }

            @Override
            public void saveOrUpdate(PaperSubmissionDraft draft) {
            }

            @Override
            public long countAll() {
                return 1;
            }
        };
    }
}
