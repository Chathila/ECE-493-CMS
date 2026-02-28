package com.ece493.cms.unit;

import com.ece493.cms.db.Db;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DbTest {
    @Test
    void createDataSourceBuildsH2DataSource() {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:db_test;DB_CLOSE_DELAY=-1");

        assertTrue(dataSource != null);
    }

    @Test
    void runSchemaWrapsSqlException() {
        assertThrows(IllegalStateException.class, () -> Db.runSchema(failingDataSource()));
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
