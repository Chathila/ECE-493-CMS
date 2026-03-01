package com.ece493.cms.repository;

import com.ece493.cms.model.PaperSubmissionDraft;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPaperSubmissionDraftRepository implements PaperSubmissionDraftRepository {
    private final DataSource dataSource;

    public JdbcPaperSubmissionDraftRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<PaperSubmissionDraft> findByIdAndAuthorEmail(long draftId, String authorEmail) {
        String sql = "SELECT draft_id, author_email, title, authors, affiliations, abstract_text, keywords, contact_details, updated_at FROM paper_submission_drafts WHERE draft_id = ? AND author_email = ?";
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, draftId);
            statement.setString(2, authorEmail);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                resultSet.close();
                statement.close();
                connection.close();
                return Optional.empty();
            }
            PaperSubmissionDraft draft = readDraft(resultSet);
            resultSet.close();
            statement.close();
            connection.close();
            return Optional.of(draft);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch paper submission draft", e);
        }
    }

    @Override
    public List<PaperSubmissionDraft> findAllByAuthorEmail(String authorEmail) {
        String sql = "SELECT draft_id, author_email, title, authors, affiliations, abstract_text, keywords, contact_details, updated_at FROM paper_submission_drafts WHERE author_email = ? ORDER BY updated_at DESC, draft_id DESC";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, authorEmail);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<PaperSubmissionDraft> drafts = new ArrayList<>();
                while (resultSet.next()) {
                    drafts.add(readDraft(resultSet));
                }
                return drafts;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to list paper submission drafts", e);
        }
    }

    @Override
    public long save(PaperSubmissionDraft draft) {
        String sql = "INSERT INTO paper_submission_drafts (author_email, title, authors, affiliations, abstract_text, keywords, contact_details, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, draft.getAuthorEmail());
            statement.setString(2, draft.getTitle());
            statement.setString(3, draft.getAuthors());
            statement.setString(4, draft.getAffiliations());
            statement.setString(5, draft.getPaperAbstract());
            statement.setString(6, draft.getKeywords());
            statement.setString(7, draft.getContactDetails());
            statement.setTimestamp(8, Timestamp.from(draft.getUpdatedAt()));
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                long generatedId = keys.getLong(1);
                keys.close();
                statement.close();
                connection.close();
                return generatedId;
            }
            keys.close();
            statement.close();
            connection.close();
            throw new IllegalStateException("Failed to read generated draft id");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save paper submission draft", e);
        }
    }

    @Override
    public boolean update(PaperSubmissionDraft draft) {
        String sql = "UPDATE paper_submission_drafts SET title = ?, authors = ?, affiliations = ?, abstract_text = ?, keywords = ?, contact_details = ?, updated_at = ? WHERE draft_id = ? AND author_email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, draft.getTitle());
            statement.setString(2, draft.getAuthors());
            statement.setString(3, draft.getAffiliations());
            statement.setString(4, draft.getPaperAbstract());
            statement.setString(5, draft.getKeywords());
            statement.setString(6, draft.getContactDetails());
            statement.setTimestamp(7, Timestamp.from(draft.getUpdatedAt()));
            statement.setLong(8, draft.getDraftId());
            statement.setString(9, draft.getAuthorEmail());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update paper submission draft", e);
        }
    }

    @Override
    public boolean deleteByIdAndAuthorEmail(long draftId, String authorEmail) {
        String sql = "DELETE FROM paper_submission_drafts WHERE draft_id = ? AND author_email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, draftId);
            statement.setString(2, authorEmail);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete paper submission draft", e);
        }
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(1) FROM paper_submission_drafts";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to count paper submission drafts", e);
        }
    }

    private PaperSubmissionDraft readDraft(ResultSet resultSet) throws SQLException {
        return new PaperSubmissionDraft(
                resultSet.getLong("draft_id"),
                resultSet.getString("author_email"),
                resultSet.getString("title"),
                resultSet.getString("authors"),
                resultSet.getString("affiliations"),
                resultSet.getString("abstract_text"),
                resultSet.getString("keywords"),
                resultSet.getString("contact_details"),
                resultSet.getTimestamp("updated_at").toInstant()
        );
    }
}
