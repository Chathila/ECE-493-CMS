package com.ece493.cms.unit;

import com.ece493.cms.db.Db;
import com.ece493.cms.repository.JdbcRefereeAssignmentRepository;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JdbcRefereeAssignmentRepositoryTest {
    @Test
    void savesAndCountsAssignments() {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:ref_assign_repo;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        JdbcRefereeAssignmentRepository repository = new JdbcRefereeAssignmentRepository(dataSource);

        repository.saveAssignments("1", List.of("ref1@cms.com", "ref2@cms.com", "ref1@cms.com"));

        assertEquals(2L, repository.countAssignmentsByRefereeEmail("ref1@cms.com"));
        assertEquals(1L, repository.countAssignmentsByRefereeEmail("ref2@cms.com"));
        assertEquals(3L, repository.countAssignmentsByPaperId("1"));
        assertEquals(0L, repository.countAssignmentsByPaperId("2"));
    }

    @Test
    void wrapsSqlErrors() {
        JdbcRefereeAssignmentRepository repository = new JdbcRefereeAssignmentRepository(failingDataSource());

        assertThrows(IllegalStateException.class, () -> repository.saveAssignments("1", List.of("ref@cms.com")));
        assertThrows(IllegalStateException.class, () -> repository.countAssignmentsByRefereeEmail("ref@cms.com"));
        assertThrows(IllegalStateException.class, () -> repository.countAssignmentsByPaperId("1"));
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
