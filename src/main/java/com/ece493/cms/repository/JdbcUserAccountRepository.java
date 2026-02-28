package com.ece493.cms.repository;

import com.ece493.cms.model.UserAccount;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

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
    public Optional<UserAccount> findByEmail(String email) {
        String sql = "SELECT user_id, full_name, email, password_hash, password_salt, status, role, created_at FROM user_accounts WHERE email = ?";
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                resultSet.close();
                statement.close();
                connection.close();
                return Optional.empty();
            }
            UserAccount account = new UserAccount(
                    resultSet.getLong("user_id"),
                    resultSet.getString("email"),
                    resultSet.getString("password_hash"),
                    resultSet.getString("password_salt"),
                    resultSet.getString("status"),
                    resultSet.getString("role"),
                    resultSet.getString("full_name"),
                    resultSet.getTimestamp("created_at").toInstant()
            );
            resultSet.close();
            statement.close();
            connection.close();
            return Optional.of(account);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch user by email", e);
        }
    }

    @Override
    public void save(UserAccount userAccount) {
        String sql = "INSERT INTO user_accounts (full_name, email, password_hash, password_salt, status, role, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userAccount.getFullName());
            statement.setString(2, userAccount.getEmail());
            statement.setString(3, userAccount.getPasswordHash());
            statement.setString(4, userAccount.getPasswordSalt());
            statement.setString(5, userAccount.getStatus());
            statement.setString(6, userAccount.getRole());
            statement.setTimestamp(7, Timestamp.from(userAccount.getCreatedAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save user account", e);
        }
    }

    @Override
    public boolean updatePasswordCredentialsByEmail(String email, String passwordHash, String passwordSalt) {
        String sql = "UPDATE user_accounts SET password_hash = ?, password_salt = ? WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordHash);
            statement.setString(2, passwordSalt);
            statement.setString(3, email);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update password credentials", e);
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
