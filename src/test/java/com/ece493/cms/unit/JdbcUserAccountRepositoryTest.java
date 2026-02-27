package com.ece493.cms.unit;

import com.ece493.cms.db.Db;
import com.ece493.cms.model.UserAccount;
import com.ece493.cms.repository.JdbcUserAccountRepository;
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
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class JdbcUserAccountRepositoryTest {
    @Test
    void existsByEmailReturnsTrueAndFalse() {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:repo_exists;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        JdbcUserAccountRepository repository = new JdbcUserAccountRepository(dataSource);

        repository.save(user("exists@cms.com", "ACTIVE", "AUTHOR"));

        assertTrue(repository.existsByEmail("exists@cms.com"));
        assertFalse(repository.existsByEmail("missing@cms.com"));
    }

    @Test
    void findByEmailReturnsRecordWhenPresentAndEmptyWhenMissing() {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:repo_find;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        JdbcUserAccountRepository repository = new JdbcUserAccountRepository(dataSource);

        repository.save(user("present@cms.com", "ACTIVE", "PROGRAM_CHAIR"));

        Optional<UserAccount> present = repository.findByEmail("present@cms.com");
        Optional<UserAccount> missing = repository.findByEmail("absent@cms.com");

        assertTrue(present.isPresent());
        assertEquals("PROGRAM_CHAIR", present.get().getRole());
        assertTrue(missing.isEmpty());
    }

    @Test
    void countByEmailReturnsExpectedCount() {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:repo_count;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        JdbcUserAccountRepository repository = new JdbcUserAccountRepository(dataSource);

        repository.save(user("one@cms.com", "ACTIVE", "AUTHOR"));

        assertEquals(1, repository.countByEmail("one@cms.com"));
        assertEquals(0, repository.countByEmail("none@cms.com"));
    }

    @Test
    void updatePasswordCredentialsByEmailUpdatesOneRecordAndReturnsFalseForMissing() {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:repo_update;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        JdbcUserAccountRepository repository = new JdbcUserAccountRepository(dataSource);

        repository.save(user("update@cms.com", "ACTIVE", "AUTHOR"));

        assertTrue(repository.updatePasswordCredentialsByEmail("update@cms.com", "newHash", "newSalt"));
        assertFalse(repository.updatePasswordCredentialsByEmail("missing@cms.com", "newHash", "newSalt"));
    }

    @Test
    void existsByEmailWrapsSqlExceptions() {
        JdbcUserAccountRepository repository = new JdbcUserAccountRepository(failingDataSource());

        assertThrows(IllegalStateException.class, () -> repository.existsByEmail("x@cms.com"));
    }

    @Test
    void findByEmailWrapsSqlExceptions() {
        JdbcUserAccountRepository repository = new JdbcUserAccountRepository(failingDataSource());

        assertThrows(IllegalStateException.class, () -> repository.findByEmail("x@cms.com"));
    }

    @Test
    void saveWrapsSqlExceptions() {
        JdbcUserAccountRepository repository = new JdbcUserAccountRepository(failingDataSource());

        assertThrows(IllegalStateException.class, () -> repository.save(user("x@cms.com", "ACTIVE", "AUTHOR")));
    }

    @Test
    void countByEmailWrapsSqlExceptions() {
        JdbcUserAccountRepository repository = new JdbcUserAccountRepository(failingDataSource());

        assertThrows(IllegalStateException.class, () -> repository.countByEmail("x@cms.com"));
    }

    @Test
    void updatePasswordCredentialsByEmailWrapsSqlExceptions() {
        JdbcUserAccountRepository repository = new JdbcUserAccountRepository(failingDataSource());

        assertThrows(IllegalStateException.class, () -> repository.updatePasswordCredentialsByEmail("x@cms.com", "h", "s"));
    }

    @Test
    void findByEmailWrapsSqlExceptionThrownDuringStatementClose() {
        JdbcUserAccountRepository repository = new JdbcUserAccountRepository(closeFailingDataSource());

        assertThrows(IllegalStateException.class, () -> repository.findByEmail("x@cms.com"));
    }

    @Test
    void findByEmailPropagatesNonSqlFailuresFromResourceClose() {
        JdbcUserAccountRepository repository = new JdbcUserAccountRepository(runtimeCloseFailingDataSource());

        assertThrows(RuntimeException.class, () -> repository.findByEmail("x@cms.com"));
    }

    @Test
    void findByEmailWrapsPrimarySqlExceptionEvenWhenCloseAlsoFails() {
        JdbcUserAccountRepository repository = new JdbcUserAccountRepository(primaryAndCloseFailingDataSource());

        assertThrows(IllegalStateException.class, () -> repository.findByEmail("x@cms.com"));
    }

    private UserAccount user(String email, String status, String role) {
        return new UserAccount(0L, email, "hash", "salt", status, role, Instant.now());
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
                // No-op
            }

            @Override
            public void setLoginTimeout(int seconds) {
                // No-op
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

    private DataSource closeFailingDataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() {
                return (Connection) Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class[]{Connection.class},
                        (proxy, method, args) -> {
                            if ("prepareStatement".equals(method.getName())) {
                                return closeFailingStatement();
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
                // No-op
            }

            @Override
            public void setLoginTimeout(int seconds) {
                // No-op
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

    private DataSource runtimeCloseFailingDataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() {
                return (Connection) Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class[]{Connection.class},
                        (proxy, method, args) -> {
                            if ("prepareStatement".equals(method.getName())) {
                                return runtimeCloseFailingStatement();
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
                // No-op
            }

            @Override
            public void setLoginTimeout(int seconds) {
                // No-op
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

    private DataSource primaryAndCloseFailingDataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() {
                return (Connection) Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class[]{Connection.class},
                        (proxy, method, args) -> {
                            if ("prepareStatement".equals(method.getName())) {
                                return primaryAndCloseFailingStatement();
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
                // No-op
            }

            @Override
            public void setLoginTimeout(int seconds) {
                // No-op
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

    private PreparedStatement closeFailingStatement() {
        return (PreparedStatement) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{PreparedStatement.class},
                (proxy, method, args) -> {
                    if ("setString".equals(method.getName())) {
                        return null;
                    }
                    if ("executeQuery".equals(method.getName())) {
                        return emptyResultSet();
                    }
                    if ("close".equals(method.getName())) {
                        throw new SQLException("close failed");
                    }
                    throw new UnsupportedOperationException(method.getName());
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

    private PreparedStatement runtimeCloseFailingStatement() {
        return (PreparedStatement) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{PreparedStatement.class},
                (proxy, method, args) -> {
                    if ("setString".equals(method.getName())) {
                        return null;
                    }
                    if ("executeQuery".equals(method.getName())) {
                        return emptyResultSet();
                    }
                    if ("close".equals(method.getName())) {
                        throw new RuntimeException("runtime close failed");
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    private PreparedStatement primaryAndCloseFailingStatement() {
        return (PreparedStatement) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{PreparedStatement.class},
                (proxy, method, args) -> {
                    if ("setString".equals(method.getName())) {
                        return null;
                    }
                    if ("executeQuery".equals(method.getName())) {
                        throw new SQLException("primary query failure");
                    }
                    if ("close".equals(method.getName())) {
                        throw new SQLException("close failure");
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );
    }
}
