package com.ece493.cms.unit;

import com.ece493.cms.controller.PaperSubmissionListServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.repository.PaperSubmissionRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaperSubmissionListServletTest {
    @Test
    void returnsUnauthorizedWhenSessionMissing() throws Exception {
        PaperSubmissionListServlet servlet = new PaperSubmissionListServlet(repo(List.of()));
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(), response.asResponse());

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("log in"));
    }

    @Test
    void returnsUnauthorizedWhenSessionEmailBlank() throws Exception {
        PaperSubmissionListServlet servlet = new PaperSubmissionListServlet(repo(List.of()));
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", " ");

        servlet.service(ServletHttpTestSupport.getRequest(session), response.asResponse());

        assertEquals(401, response.getStatus());
    }

    @Test
    void returnsSubmissionListForLoggedInAuthor() throws Exception {
        PaperSubmissionListServlet servlet = new PaperSubmissionListServlet(repo(List.of(
                new PaperSubmission(2L, "author@cms.com", "Paper B", "A", "U", "X", "k", "author@cms.com", 2L, Instant.now()),
                new PaperSubmission(1L, "author@cms.com", "Paper A", "A", "U", "X", "k", "author@cms.com", 1L, Instant.now())
        )));
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "author@cms.com");

        servlet.service(ServletHttpTestSupport.getRequest(session), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"submission_id\":2"));
        assertTrue(response.getBody().contains("Paper A"));
    }

    @Test
    void returnsSubmissionListWithNullFieldsAsEmptyStrings() throws Exception {
        PaperSubmissionListServlet servlet = new PaperSubmissionListServlet(repo(List.of(
                new PaperSubmission(1L, "author@cms.com", null, "A", "U", "X", "k", null, 1L, Instant.now())
        )));
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "author@cms.com");

        servlet.service(ServletHttpTestSupport.getRequest(session), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"title\":\"\""));
        assertTrue(response.getBody().contains("\"contact_details\":\"\""));
    }

    private PaperSubmissionRepository repo(List<PaperSubmission> submissions) {
        return new PaperSubmissionRepository() {
            @Override
            public long save(PaperSubmission paperSubmission) {
                return 0;
            }

            @Override
            public List<PaperSubmission> findAllByAuthorEmail(String authorEmail) {
                return submissions;
            }

            @Override
            public long countAll() {
                return submissions.size();
            }
        };
    }
}
