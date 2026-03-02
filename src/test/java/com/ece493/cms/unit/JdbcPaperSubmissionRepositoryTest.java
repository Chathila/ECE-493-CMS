package com.ece493.cms.unit;

import com.ece493.cms.db.Db;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.repository.JdbcPaperSubmissionRepository;
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
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcPaperSubmissionRepositoryTest {
    @Test
    void savesAndCountsSubmissions() {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:paper_repo;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        JdbcPaperSubmissionRepository repository = new JdbcPaperSubmissionRepository(dataSource);

        long firstId = repository.save(new PaperSubmission(0L, "author@cms.com", "Title", "A", "U", "Abstract", "k", "author@cms.com", 1L, Instant.now()));
        long secondId = repository.save(new PaperSubmission(0L, "author@cms.com", "Title 2", "A", "U", "Abstract", "k", "author@cms.com", 2L, Instant.now()));

        assertEquals(2L, repository.countAll());
        assertEquals(2L, repository.findAllByAuthorEmail("author@cms.com").size());
        assertEquals(0L, repository.findAllByAuthorEmail("nobody@cms.com").size());
        assertTrue(repository.findBySubmissionId(firstId).isPresent());
        assertTrue(repository.findBySubmissionId(99999L).isEmpty());
        assertTrue(secondId > firstId);
    }

    @Test
    void wrapsSqlErrors() {
        JdbcPaperSubmissionRepository repository = new JdbcPaperSubmissionRepository(failingDataSource());

        assertThrows(IllegalStateException.class, () -> repository.save(new PaperSubmission(0L, "a", "t", "a", "u", "ab", "k", "c", 1L, Instant.now())));
        assertThrows(IllegalStateException.class, () -> repository.findAllByAuthorEmail("a"));
        assertThrows(IllegalStateException.class, () -> repository.findBySubmissionId(1L));
        assertThrows(IllegalStateException.class, repository::countAll);
    }

    @Test
    void saveThrowsWhenGeneratedKeysMissing() {
        JdbcPaperSubmissionRepository repository = new JdbcPaperSubmissionRepository(noGeneratedKeysDataSource());

        assertThrows(IllegalStateException.class, () -> repository.save(new PaperSubmission(
                0L, "a@cms.com", "T", "A", "U", "Ab", "k", "a@cms.com", 1L, Instant.now()
        )));
    }

    @Test
    void findBySubmissionIdWrapsExecuteQueryFailures() {
        JdbcPaperSubmissionRepository repository = new JdbcPaperSubmissionRepository(executeQueryFailureDataSource());

        assertThrows(IllegalStateException.class, () -> repository.findBySubmissionId(1L));
    }

    @Test
    void findBySubmissionIdReturnsEmptyWhenNoRows() {
        JdbcPaperSubmissionRepository repository = new JdbcPaperSubmissionRepository(resultSetCloseFailureDataSource());

        assertTrue(repository.findBySubmissionId(1L).isEmpty());
    }

    @Test
    void findBySubmissionIdWrapsStatementCloseFailures() {
        JdbcPaperSubmissionRepository repository = new JdbcPaperSubmissionRepository(statementCloseFailureDataSource());

        assertTrue(repository.findBySubmissionId(1L).isEmpty());
    }

    @Test
    void findBySubmissionIdWrapsStatementCloseFailuresAfterRowRead() {
        JdbcPaperSubmissionRepository repository = new JdbcPaperSubmissionRepository(statementCloseFailureWithRowDataSource());

        assertTrue(repository.findBySubmissionId(1L).isPresent());
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

    private DataSource executeQueryFailureDataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() {
                return (Connection) Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class[]{Connection.class},
                        (proxy, method, args) -> {
                            if ("prepareStatement".equals(method.getName())) {
                                return executeQueryFailureStatement();
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

    private PreparedStatement executeQueryFailureStatement() {
        return (PreparedStatement) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{PreparedStatement.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if (name.startsWith("set")) {
                        return null;
                    }
                    if ("executeQuery".equals(name)) {
                        throw new SQLException("query failed");
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

    private DataSource resultSetCloseFailureDataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() {
                return (Connection) Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class[]{Connection.class},
                        (proxy, method, args) -> {
                            if ("prepareStatement".equals(method.getName())) {
                                return resultSetCloseFailureStatement();
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

    private PreparedStatement resultSetCloseFailureStatement() {
        return (PreparedStatement) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{PreparedStatement.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if (name.startsWith("set")) {
                        return null;
                    }
                    if ("executeQuery".equals(name)) {
                        return resultSetWithCloseFailure();
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

    private ResultSet resultSetWithCloseFailure() {
        return (ResultSet) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{ResultSet.class},
                (proxy, method, args) -> {
                    if ("next".equals(method.getName())) {
                        return false;
                    }
                    if ("close".equals(method.getName())) {
                        throw new SQLException("close failed");
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    private DataSource statementCloseFailureDataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() {
                return (Connection) Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class[]{Connection.class},
                        (proxy, method, args) -> {
                            if ("prepareStatement".equals(method.getName())) {
                                return statementWithCloseFailure();
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

    private PreparedStatement statementWithCloseFailure() {
        return (PreparedStatement) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{PreparedStatement.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if (name.startsWith("set")) {
                        return null;
                    }
                    if ("executeQuery".equals(name)) {
                        return emptyResultSet();
                    }
                    if ("close".equals(name)) {
                        throw new SQLException("statement close failed");
                    }
                    if ("isClosed".equals(name)) {
                        return false;
                    }
                    throw new UnsupportedOperationException(name);
                }
        );
    }

    private DataSource statementCloseFailureWithRowDataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() {
                return (Connection) Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class[]{Connection.class},
                        (proxy, method, args) -> {
                            if ("prepareStatement".equals(method.getName())) {
                                return statementWithCloseFailureAndRow();
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

    private PreparedStatement statementWithCloseFailureAndRow() {
        return (PreparedStatement) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{PreparedStatement.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if (name.startsWith("set")) {
                        return null;
                    }
                    if ("executeQuery".equals(name)) {
                        return singleRowResultSet();
                    }
                    if ("close".equals(name)) {
                        throw new SQLException("statement close failed");
                    }
                    if ("isClosed".equals(name)) {
                        return false;
                    }
                    throw new UnsupportedOperationException(name);
                }
        );
    }

    private ResultSet singleRowResultSet() {
        return (ResultSet) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{ResultSet.class},
                new java.lang.reflect.InvocationHandler() {
                    private boolean read;

                    @Override
                    public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
                        String name = method.getName();
                        if ("next".equals(name)) {
                            if (!read) {
                                read = true;
                                return true;
                            }
                            return false;
                        }
                        if ("getLong".equals(name)) {
                            return 1L;
                        }
                        if ("getString".equals(name)) {
                            return "v";
                        }
                        if ("getTimestamp".equals(name)) {
                            return java.sql.Timestamp.from(Instant.now());
                        }
                        if ("close".equals(name)) {
                            return null;
                        }
                        throw new UnsupportedOperationException(name);
                    }
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
}
