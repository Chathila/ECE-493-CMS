package com.ece493.cms.unit;

import com.ece493.cms.controller.PaperDetailsServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.ManuscriptFile;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.repository.PaperSubmissionRepository;
import com.ece493.cms.service.FileStorageService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaperDetailsServletTest {
    @Test
    void requiresLoginForPaperDetails() throws Exception {
        PaperDetailsServlet servlet = new PaperDetailsServlet(repo(List.of()), storage(Optional.empty()));
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(null, null, "/papers/details/1"), response.asResponse());

        assertEquals(401, response.getStatus());
    }

    @Test
    void returnsPaperDetailsWithDownloadUrl() throws Exception {
        PaperDetailsServlet servlet = new PaperDetailsServlet(
                repo(List.of(new PaperSubmission(
                        1L, "author@cms.com", "T", "A", "U", "Abs", "k", "c", 55L, Instant.now()
                ))),
                storage(Optional.empty())
        );
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "ref@cms.com");

        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/papers/details/1"), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"download_url\":\"/papers/files/55\""));
        assertTrue(response.getBody().contains("\"title\":\"T\""));
    }

    @Test
    void returnsFileDownloadWhenAvailable() throws Exception {
        PaperDetailsServlet servlet = new PaperDetailsServlet(
                repo(List.of()),
                storage(Optional.of(new ManuscriptFile("paper.pdf", "ZGF0YQ==")))
        );
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "ref@cms.com");

        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/papers/files/55"), response.asResponse());

        assertEquals(200, response.getStatus());
        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.getBody().contains("data"));
    }

    @Test
    void handlesMissingAndBadPaths() throws Exception {
        PaperDetailsServlet servlet = new PaperDetailsServlet(repo(List.of()), storage(Optional.empty()));
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "ref@cms.com");
        ServletHttpTestSupport.ResponseCapture missingPaper = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture missingFile = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture badPath = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/papers/details/99"), missingPaper.asResponse());
        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/papers/files/88"), missingFile.asResponse());
        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/papers/details/not-a-number"), badPath.asResponse());

        assertEquals(404, missingPaper.getStatus());
        assertEquals(404, missingFile.getStatus());
        assertEquals(400, badPath.getStatus());
    }

    @Test
    void handlesNullUriAndBlankSessionEmail() throws Exception {
        PaperDetailsServlet servlet = new PaperDetailsServlet(repo(List.of()), storage(Optional.empty()));
        ServletHttpTestSupport.SessionCapture blankSession = ServletHttpTestSupport.sessionCapture();
        blankSession.asSession().setAttribute("user_email", " ");
        ServletHttpTestSupport.ResponseCapture blankUser = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "ref@cms.com");
        ServletHttpTestSupport.ResponseCapture nullUri = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(blankSession, null, "/papers/details/1"), blankUser.asResponse());
        servlet.service(ServletHttpTestSupport.getRequest(session), nullUri.asResponse());

        assertEquals(401, blankUser.getStatus());
        assertEquals(400, nullUri.getStatus());
    }

    @Test
    void handlesInvalidBase64DocxAndUnknownFileType() throws Exception {
        PaperDetailsServlet servlet = new PaperDetailsServlet(
                repo(List.of()),
                new FileStorageService() {
                    @Override
                    public boolean isSupportedFormat(String filename) {
                        return true;
                    }

                    @Override
                    public boolean isWithinSizeLimit(long sizeBytes) {
                        return true;
                    }

                    @Override
                    public long computeFileSizeBytes(String contentBase64) {
                        return 0;
                    }

                    @Override
                    public long store(ManuscriptFile manuscriptFile) {
                        return 0;
                    }

                    @Override
                    public Optional<ManuscriptFile> findById(long fileId) {
                        if (fileId == 1L) {
                            return Optional.of(new ManuscriptFile("paper.docx", "ZGF0YQ=="));
                        }
                        if (fileId == 2L) {
                            return Optional.of(new ManuscriptFile("paper.bin", "ZGF0YQ=="));
                        }
                        if (fileId == 3L) {
                            return Optional.of(new ManuscriptFile(null, "ZGF0YQ=="));
                        }
                        if (fileId == 4L) {
                            return Optional.of(new ManuscriptFile("paper.pdf", "%%%"));
                        }
                        return Optional.empty();
                    }
                }
        );
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "ref@cms.com");

        ServletHttpTestSupport.ResponseCapture docx = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture octet = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture nullName = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture invalid = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/papers/files/1"), docx.asResponse());
        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/papers/files/2"), octet.asResponse());
        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/papers/files/3"), nullName.asResponse());
        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/papers/files/4"), invalid.asResponse());

        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", docx.getContentType());
        assertEquals("application/octet-stream", octet.getContentType());
        assertEquals("application/octet-stream", nullName.getContentType());
        assertEquals(500, invalid.getStatus());
    }

    private PaperSubmissionRepository repo(List<PaperSubmission> papers) {
        return new PaperSubmissionRepository() {
            @Override
            public long save(PaperSubmission paperSubmission) {
                return 0;
            }

            @Override
            public List<PaperSubmission> findAllByAuthorEmail(String authorEmail) {
                return papers;
            }

            @Override
            public Optional<PaperSubmission> findBySubmissionId(long submissionId) {
                return papers.stream().filter(v -> v.getSubmissionId() == submissionId).findFirst();
            }

            @Override
            public long countAll() {
                return papers.size();
            }
        };
    }

    private FileStorageService storage(Optional<ManuscriptFile> file) {
        return new FileStorageService() {
            @Override
            public boolean isSupportedFormat(String filename) {
                return true;
            }

            @Override
            public boolean isWithinSizeLimit(long sizeBytes) {
                return true;
            }

            @Override
            public long computeFileSizeBytes(String contentBase64) {
                return 0;
            }

            @Override
            public long store(ManuscriptFile manuscriptFile) {
                return 0;
            }

            @Override
            public Optional<ManuscriptFile> findById(long fileId) {
                return file;
            }
        };
    }
}
