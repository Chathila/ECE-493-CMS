package com.ece493.cms.repository;

import com.ece493.cms.model.UserAccount;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class JdbcUserAccountRepository implements UserAccountRepository {
    private final DataSource dataSource;

    public JdbcUserAccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(1) FROM user_accounts WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1) > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to query user by email", e);
        }
    }

    @Override
    public void save(UserAccount userAccount) {
        String sql = "INSERT INTO user_accounts (email, password_hash, password_salt, status, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userAccount.getEmail());
            statement.setString(2, userAccount.getPasswordHash());
            statement.setString(3, userAccount.getPasswordSalt());
            statement.setString(4, userAccount.getStatus());
            statement.setTimestamp(5, Timestamp.from(userAccount.getCreatedAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save user account", e);
        }
    }

    @Override
    public long countByEmail(String email) {
        String sql = "SELECT COUNT(1) FROM user_accounts WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to count user by email", e);
        }
    }
}
