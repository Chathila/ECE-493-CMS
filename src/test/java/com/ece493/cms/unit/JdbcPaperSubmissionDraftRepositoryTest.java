package com.ece493.cms.unit;

import com.ece493.cms.db.Db;
import com.ece493.cms.model.PaperSubmissionDraft;
import com.ece493.cms.repository.JdbcPaperSubmissionDraftRepository;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcPaperSubmissionDraftRepositoryTest {
    @Test
    void savesMultipleDraftsAndListsByAuthor() {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:draft_repo;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        JdbcPaperSubmissionDraftRepository repository = new JdbcPaperSubmissionDraftRepository(dataSource);

        long firstId = repository.save(draft(0L, "author@cms.com", "Title A"));
        long secondId = repository.save(draft(0L, "author@cms.com", "Title B"));

        List<PaperSubmissionDraft> loaded = repository.findAllByAuthorEmail("author@cms.com");
        Optional<PaperSubmissionDraft> fetched = repository.findByIdAndAuthorEmail(firstId, "author@cms.com");

        assertEquals(2L, repository.countAll());
        assertEquals(2, loaded.size());
        assertEquals("Title B", loaded.get(0).getTitle());
        assertEquals("Title A", loaded.get(1).getTitle());
        assertTrue(fetched.isPresent());
        assertEquals("Title A", fetched.get().getTitle());
        assertTrue(secondId > firstId);
    }

    @Test
    void updatesExistingDraftByIdAndAuthor() {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:draft_repo_update;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        JdbcPaperSubmissionDraftRepository repository = new JdbcPaperSubmissionDraftRepository(dataSource);

        long id = repository.save(draft(0L, "author@cms.com", "Title A"));
        boolean updated = repository.update(draft(id, "author@cms.com", "Title Updated"));
        boolean notUpdatedWrongAuthor = repository.update(draft(id, "other@cms.com", "Nope"));

        Optional<PaperSubmissionDraft> fetched = repository.findByIdAndAuthorEmail(id, "author@cms.com");
        assertTrue(updated);
        assertTrue(!notUpdatedWrongAuthor);
        assertTrue(fetched.isPresent());
        assertEquals("Title Updated", fetched.get().getTitle());
    }

    @Test
    void deletesExistingDraftByIdAndAuthor() {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:draft_repo_delete;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        JdbcPaperSubmissionDraftRepository repository = new JdbcPaperSubmissionDraftRepository(dataSource);

        long id = repository.save(draft(0L, "author@cms.com", "Title A"));
        boolean deleted = repository.deleteByIdAndAuthorEmail(id, "author@cms.com");
        boolean missingDelete = repository.deleteByIdAndAuthorEmail(id, "author@cms.com");

        assertTrue(deleted);
        assertTrue(!missingDelete);
        assertEquals(0L, repository.countAll());
    }

    @Test
    void wrapsSqlErrors() {
        JdbcPaperSubmissionDraftRepository repository = new JdbcPaperSubmissionDraftRepository(failingDataSource());

        assertThrows(IllegalStateException.class, () -> repository.findByIdAndAuthorEmail(1L, "a@b.com"));
        assertThrows(IllegalStateException.class, () -> repository.findAllByAuthorEmail("a@b.com"));
        assertThrows(IllegalStateException.class, () -> repository.save(draft(0L, "a@b.com", "T")));
        assertThrows(IllegalStateException.class, () -> repository.update(draft(1L, "a@b.com", "T")));
        assertThrows(IllegalStateException.class, () -> repository.deleteByIdAndAuthorEmail(1L, "a@b.com"));
        assertThrows(IllegalStateException.class, repository::countAll);
    }

    @Test
    void saveThrowsWhenGeneratedKeysMissing() {
        JdbcPaperSubmissionDraftRepository repository = new JdbcPaperSubmissionDraftRepository(noGeneratedKeysDataSource());

        assertThrows(IllegalStateException.class, () -> repository.save(draft(0L, "a@b.com", "T")));
    }

    @Test
    void findByIdWrapsSQLExceptionThrownDuringResultSetClose() {
        JdbcPaperSubmissionDraftRepository repository = new JdbcPaperSubmissionDraftRepository(closeFailingFindDataSource());

        assertThrows(IllegalStateException.class, () -> repository.findByIdAndAuthorEmail(1L, "a@b.com"));
    }

    private PaperSubmissionDraft draft(long draftId, String authorEmail, String title) {
        return new PaperSubmissionDraft(draftId, authorEmail, title, "A", "U", "Abstract", "k", authorEmail, Instant.now());
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

    private DataSource noGeneratedKeysDataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() {
                return (Connection) Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class[]{Connection.class},
                        (proxy, method, args) -> {
                            if ("prepareStatement".equals(method.getName())) {
                                return noKeysStatement();
                            }
                            if ("close".equals(method.getName())) {
                                return null;
                            }
                            if ("isClosed".equals(method.getName())) {
                                return false;
                            }
                            throw new UnsupportedOperationException(method.getName());
                        }
                );
            }

            @Override
            public Connection getConnection(String username, String password) {
                return getConnection();
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

    private PreparedStatement noKeysStatement() {
        return (PreparedStatement) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{PreparedStatement.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if (name.startsWith("set")) {
                        return null;
                    }
                    if ("executeUpdate".equals(name)) {
                        return 1;
                    }
                    if ("getGeneratedKeys".equals(name)) {
                        return emptyResultSet();
                    }
                    if ("close".equals(name)) {
                        return null;
                    }
                    if ("isClosed".equals(name)) {
                        return false;
                    }
                    throw new UnsupportedOperationException(name);
                }
        );
    }

    private ResultSet emptyResultSet() {
        return (ResultSet) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{ResultSet.class},
                (proxy, method, args) -> {
                    if ("next".equals(method.getName())) {
                        return false;
                    }
                    if ("close".equals(method.getName())) {
                        return null;
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    private DataSource closeFailingFindDataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() {
                return (Connection) Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class[]{Connection.class},
                        (proxy, method, args) -> {
                            if ("prepareStatement".equals(method.getName())) {
                                return closeFailingFindStatement();
                            }
                            if ("close".equals(method.getName())) {
                                return null;
                            }
                            if ("isClosed".equals(method.getName())) {
                                return false;
                            }
                            throw new UnsupportedOperationException(method.getName());
                        }
                );
            }

            @Override
            public Connection getConnection(String username, String password) {
                return getConnection();
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

    private PreparedStatement closeFailingFindStatement() {
        return (PreparedStatement) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{PreparedStatement.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if (name.startsWith("set")) {
                        return null;
                    }
                    if ("executeQuery".equals(name)) {
                        return closeFailingResultSet();
                    }
                    if ("close".equals(name)) {
                        return null;
                    }
                    if ("isClosed".equals(name)) {
                        return false;
                    }
                    throw new UnsupportedOperationException(name);
                }
        );
    }

    private ResultSet closeFailingResultSet() {
        return (ResultSet) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{ResultSet.class},
                (proxy, method, args) -> {
                    if ("next".equals(method.getName())) {
                        return false;
                    }
                    if ("close".equals(method.getName())) {
                        throw new SQLException("close fail");
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );
    }
}
