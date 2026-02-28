package com.ece493.cms.unit;

import com.ece493.cms.db.Db;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.repository.JdbcPaperSubmissionRepository;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.time.Instant;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JdbcPaperSubmissionRepositoryTest {
    @Test
    void savesAndCountsSubmissions() {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:paper_repo;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        JdbcPaperSubmissionRepository repository = new JdbcPaperSubmissionRepository(dataSource);

        repository.save(new PaperSubmission(0L, "author@cms.com", "Title", "A", "U", "Abstract", "k", "author@cms.com", 1L, Instant.now()));

        assertEquals(1L, repository.countAll());
    }

    @Test
    void wrapsSqlErrors() {
        JdbcPaperSubmissionRepository repository = new JdbcPaperSubmissionRepository(failingDataSource());

        assertThrows(IllegalStateException.class, () -> repository.save(new PaperSubmission(0L, "a", "t", "a", "u", "ab", "k", "c", 1L, Instant.now())));
        assertThrows(IllegalStateException.class, repository::countAll);
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
