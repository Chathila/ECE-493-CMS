package com.ece493.cms.unit;

import com.ece493.cms.db.Db;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.nio.charset.StandardCharsets;
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

    @Test
    void runSchemaHandlesEmptySplitSegments() {
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        ClassLoader custom = new ClassLoader(original) {
            @Override
            public InputStream getResourceAsStream(String name) {
                if ("db/schema.sql".equals(name)) {
                    String sql = "CREATE TABLE IF NOT EXISTS t1 (id INT);;CREATE TABLE IF NOT EXISTS t2 (id INT);";
                    return new ByteArrayInputStream(sql.getBytes(StandardCharsets.UTF_8));
                }
                return super.getResourceAsStream(name);
            }
        };
        Thread.currentThread().setContextClassLoader(custom);
        try {
            DataSource dataSource = Db.createDataSource("jdbc:h2:mem:db_schema_segments;DB_CLOSE_DELAY=-1");
            // Verifies schema parsing tolerates empty commands from consecutive ';'
            Db.runSchema(dataSource);
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
                statement.execute("SELECT COUNT(1) FROM t1");
                statement.execute("SELECT COUNT(1) FROM t2");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
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
