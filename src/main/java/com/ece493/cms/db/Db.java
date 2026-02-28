package com.ece493.cms.db;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Db {
    private Db() {
    }

    public static DataSource createDataSource(String url) {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(url);
        dataSource.setUser("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    public static void runSchema(DataSource dataSource) {
        String schemaSql = loadResource("db/schema.sql");
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            for (String sql : Arrays.stream(schemaSql.split(";"))
                    .map(String::trim)
                    .filter(command -> !command.isEmpty())
                    .toList()) {
                statement.execute(sql);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize schema", e);
        }
    }

    private static String loadResource(String resourcePath) {
        try (InputStream inputStream = Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath),
                "Missing resource: " + resourcePath
        )) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed reading resource: " + resourcePath, e);
        }
    }
}
