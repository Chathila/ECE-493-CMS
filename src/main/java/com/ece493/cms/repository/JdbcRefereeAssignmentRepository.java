package com.ece493.cms.repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class JdbcRefereeAssignmentRepository implements RefereeAssignmentRepository {
    private final DataSource dataSource;

    public JdbcRefereeAssignmentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveAssignments(String paperId, List<String> refereeEmails) {
        String sql = "INSERT INTO referee_assignments (paper_id, referee_email, assigned_at) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            Timestamp now = Timestamp.from(Instant.now());
            for (String refereeEmail : refereeEmails) {
                statement.setString(1, paperId);
                statement.setString(2, refereeEmail);
                statement.setTimestamp(3, now);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save referee assignments", e);
        }
    }

    @Override
    public long countAssignmentsByRefereeEmail(String refereeEmail) {
        String sql = "SELECT COUNT(1) FROM referee_assignments WHERE referee_email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, refereeEmail);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to count assignments by referee email", e);
        }
    }

    @Override
    public long countAssignmentsByPaperId(String paperId) {
        String sql = "SELECT COUNT(1) FROM referee_assignments WHERE paper_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, paperId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to count assignments by paper id", e);
        }
    }
}
