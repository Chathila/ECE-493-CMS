package com.ece493.cms.unit;

import com.ece493.cms.controller.ReviewWorkflowServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.model.ReviewForm;
import com.ece493.cms.model.ReviewFormAccessResult;
import com.ece493.cms.model.ReviewSubmissionResult;
import com.ece493.cms.service.ReviewFormService;
import com.ece493.cms.service.ReviewSubmissionService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewWorkflowServletTest {
    @Test
    void servesReviewFormAndSubmissionResponses() throws Exception {
        ReviewFormService formService = new ReviewFormService(null, null, null, null) {
            @Override
            public ReviewFormAccessResult accessReviewForm(long assignmentId, String refereeEmail) {
                return ReviewFormAccessResult.success(
                        new ReviewForm("f", "v1", List.of("score")),
                        new PaperSubmission(1L, "a", "T", "A", "U", "Abs", "k", "c", 9L, Instant.now())
                );
            }
        };
        ReviewSubmissionService submissionService = new ReviewSubmissionService(null, null, null, null, null) {
            @Override
            public ReviewSubmissionResult submitReview(long assignmentId, String refereeEmail, Map<String, String> responses) {
                return ReviewSubmissionResult.created(22L);
            }
        };
        ReviewWorkflowServlet servlet = new ReviewWorkflowServlet(formService, submissionService);
        ServletHttpTestSupport.ResponseCapture getResponse = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture postResponse = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "ref@cms.com");

        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/assignments/1/review-form"), getResponse.asResponse());
        servlet.service(ServletHttpTestSupport.postJsonRequest("{\"responses\":{\"score\":\"8\"}}", session, "/assignments/1/reviews"), postResponse.asResponse());

        assertEquals(200, getResponse.getStatus());
        assertTrue(getResponse.getBody().contains("\"form\""));
        assertEquals(201, postResponse.getStatus());
        assertTrue(postResponse.getBody().contains("\"review_id\":22"));
    }

    @Test
    void handlesInvalidPathsAndErrorPayloads() throws Exception {
        ReviewFormService formService = new ReviewFormService(null, null, null, null) {
            @Override
            public ReviewFormAccessResult accessReviewForm(long assignmentId, String refereeEmail) {
                return ReviewFormAccessResult.error(403, "Access denied.");
            }
        };
        ReviewSubmissionService submissionService = new ReviewSubmissionService(null, null, null, null, null) {
            @Override
            public ReviewSubmissionResult submitReview(long assignmentId, String refereeEmail, Map<String, String> responses) {
                return ReviewSubmissionResult.validationError(List.of("score"));
            }
        };
        ReviewWorkflowServlet servlet = new ReviewWorkflowServlet(formService, submissionService);
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "ref@cms.com");
        ServletHttpTestSupport.ResponseCapture badGet = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture deniedGet = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture badPost = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture validationPost = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/assignments/x/review-form"), badGet.asResponse());
        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/assignments/1/review-form"), deniedGet.asResponse());
        servlet.service(ServletHttpTestSupport.postJsonRequest("{}", session, "/assignments/x/reviews"), badPost.asResponse());
        servlet.service(ServletHttpTestSupport.postJsonRequest("{\"responses\":{\"comments\":\"ok\"}}", session, "/assignments/1/reviews"), validationPost.asResponse());

        assertEquals(400, badGet.getStatus());
        assertEquals(403, deniedGet.getStatus());
        assertEquals(400, badPost.getStatus());
        assertEquals(400, validationPost.getStatus());
        assertTrue(validationPost.getBody().contains("\"fields\":[\"score\"]"));
    }

    @Test
    void coversNullAndUnmatchedEdgeBranches() throws Exception {
        ReviewFormService formService = new ReviewFormService(null, null, null, null) {
            @Override
            public ReviewFormAccessResult accessReviewForm(long assignmentId, String refereeEmail) {
                return ReviewFormAccessResult.error(403, null);
            }
        };
        ReviewSubmissionService submissionService = new ReviewSubmissionService(null, null, null, null, null) {
            @Override
            public ReviewSubmissionResult submitReview(long assignmentId, String refereeEmail, Map<String, String> responses) {
                return ReviewSubmissionResult.error(500, "x");
            }
        };
        ReviewWorkflowServlet servlet = new ReviewWorkflowServlet(formService, submissionService);
        ServletHttpTestSupport.ResponseCapture nullUriGet = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture unmatchedGet = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture nullMessageGet = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture emptyPost = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(), nullUriGet.asResponse());
        servlet.service(ServletHttpTestSupport.getRequest(null, null, "/other"), unmatchedGet.asResponse());
        servlet.service(ServletHttpTestSupport.getRequest(null, null, "/assignments/1/review-form"), nullMessageGet.asResponse());
        servlet.service(ServletHttpTestSupport.postJsonRequest("", null, "/assignments/9/reviews"), emptyPost.asResponse());

        assertEquals(400, nullUriGet.getStatus());
        assertEquals(400, unmatchedGet.getStatus());
        assertEquals(403, nullMessageGet.getStatus());
        assertEquals(500, emptyPost.getStatus());
        assertTrue(unmatchedGet.getBody().contains("\"message\":\"Assignment id is required.\""));
        assertTrue(nullMessageGet.getBody().contains("\"message\":\"\""));

        Method parseResponses = ReviewWorkflowServlet.class.getDeclaredMethod("parseResponses", String.class);
        parseResponses.setAccessible(true);
        assertTrue(((Map<?, ?>) parseResponses.invoke(servlet, "{\"x\":1}")).isEmpty());
        assertTrue(((Map<?, ?>) parseResponses.invoke(servlet, "{\"responses\":{\"score\":\"8\"}}")).containsKey("score"));
        assertTrue(((Map<?, ?>) parseResponses.invoke(servlet, new Object[]{null})).isEmpty());

        Method parseAssignmentId = ReviewWorkflowServlet.class.getDeclaredMethod("parseAssignmentId", String.class, String.class);
        parseAssignmentId.setAccessible(true);
        assertNull(parseAssignmentId.invoke(servlet, "/assignments/1/reviews", "^/assignments/([^/]+)/review-form$"));
    }
}
