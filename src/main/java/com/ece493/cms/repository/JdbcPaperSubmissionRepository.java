package com.ece493.cms.repository;

import com.ece493.cms.model.PaperSubmission;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class JdbcPaperSubmissionRepository implements PaperSubmissionRepository {
    private final DataSource dataSource;

    public JdbcPaperSubmissionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public long save(PaperSubmission paperSubmission) {
        String sql = "INSERT INTO paper_submissions (author_email, title, authors, affiliations, abstract_text, keywords, contact_details, manuscript_file_id, submitted_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, paperSubmission.getAuthorEmail());
            statement.setString(2, paperSubmission.getTitle());
            statement.setString(3, paperSubmission.getAuthors());
            statement.setString(4, paperSubmission.getAffiliations());
            statement.setString(5, paperSubmission.getPaperAbstract());
            statement.setString(6, paperSubmission.getKeywords());
            statement.setString(7, paperSubmission.getContactDetails());
            statement.setLong(8, paperSubmission.getManuscriptFileId());
            statement.setTimestamp(9, Timestamp.from(paperSubmission.getSubmittedAt()));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("Failed to read generated submission id");
                }
                return keys.getLong(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save paper submission", e);
        }
    }

    @Override
    public List<PaperSubmission> findAllByAuthorEmail(String authorEmail) {
        String sql = "SELECT submission_id, author_email, title, authors, affiliations, abstract_text, keywords, contact_details, manuscript_file_id, submitted_at FROM paper_submissions WHERE author_email = ? ORDER BY submitted_at DESC, submission_id DESC";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, authorEmail);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<PaperSubmission> submissions = new ArrayList<>();
                while (resultSet.next()) {
                    submissions.add(new PaperSubmission(
                            resultSet.getLong("submission_id"),
                            resultSet.getString("author_email"),
                            resultSet.getString("title"),
                            resultSet.getString("authors"),
                            resultSet.getString("affiliations"),
                            resultSet.getString("abstract_text"),
                            resultSet.getString("keywords"),
                            resultSet.getString("contact_details"),
                            resultSet.getLong("manuscript_file_id"),
                            resultSet.getTimestamp("submitted_at").toInstant()
                    ));
                }
                return submissions;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to list paper submissions", e);
        }
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(1) FROM paper_submissions";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to count paper submissions", e);
        }
    }
}
