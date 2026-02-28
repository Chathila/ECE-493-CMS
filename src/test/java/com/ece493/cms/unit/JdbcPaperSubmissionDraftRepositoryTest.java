package com.ece493.cms.unit;

import com.ece493.cms.db.Db;
import com.ece493.cms.model.PaperSubmissionDraft;
import com.ece493.cms.repository.JdbcPaperSubmissionDraftRepository;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcPaperSubmissionDraftRepositoryTest {
    @Test
    void savesUpdatesAndFindsDraft() {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:draft_repo;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        JdbcPaperSubmissionDraftRepository repository = new JdbcPaperSubmissionDraftRepository(dataSource);

        repository.saveOrUpdate(draft("author@cms.com", "Title A"));
        repository.saveOrUpdate(draft("author@cms.com", "Title B"));

        Optional<PaperSubmissionDraft> loaded = repository.findByAuthorEmail("author@cms.com");

        assertTrue(loaded.isPresent());
        assertEquals("Title B", loaded.get().getTitle());
        assertEquals(1L, repository.countAll());
    }

    @Test
    void findByAuthorEmailReturnsEmptyWhenMissing() {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:draft_repo_empty;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        JdbcPaperSubmissionDraftRepository repository = new JdbcPaperSubmissionDraftRepository(dataSource);

        assertTrue(repository.findByAuthorEmail("missing@cms.com").isEmpty());
    }

    @Test
    void wrapsSqlErrors() {
        JdbcPaperSubmissionDraftRepository repository = new JdbcPaperSubmissionDraftRepository(failingDataSource());

        assertThrows(IllegalStateException.class, () -> repository.findByAuthorEmail("a@b.com"));
        assertThrows(IllegalStateException.class, () -> repository.saveOrUpdate(draft("a@b.com", "T")));
        assertThrows(IllegalStateException.class, repository::countAll);
    }

    private PaperSubmissionDraft draft(String authorEmail, String title) {
        return new PaperSubmissionDraft(0L, authorEmail, title, "A", "U", "Abstract", "k", authorEmail, Instant.now());
    }

    private DataSource failingDataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                throw new SQLException("boom");
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                throw new SQLException("boom");
            }

            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                throw new SQLException("unsupported");
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) {
                return false;
            }

            @Override
            public PrintWriter getLogWriter() {
                return null;
            }

            @Override
            public void setLogWriter(PrintWriter out) {
            }

            @Override
            public void setLoginTimeout(int seconds) {
            }

            @Override
            public int getLoginTimeout() {
                return 0;
            }

            @Override
            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                throw new SQLFeatureNotSupportedException("unsupported");
            }
        };
    }
}
