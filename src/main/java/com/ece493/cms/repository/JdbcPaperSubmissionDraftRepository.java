package com.ece493.cms.repository;

import com.ece493.cms.model.PaperSubmissionDraft;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class JdbcPaperSubmissionDraftRepository implements PaperSubmissionDraftRepository {
    private final DataSource dataSource;

    public JdbcPaperSubmissionDraftRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<PaperSubmissionDraft> findByAuthorEmail(String authorEmail) {
        String sql = "SELECT draft_id, author_email, title, authors, affiliations, abstract_text, keywords, contact_details, updated_at FROM paper_submission_drafts WHERE author_email = ?";
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, authorEmail);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                resultSet.close();
                statement.close();
                connection.close();
                return Optional.empty();
            }
            PaperSubmissionDraft draft = new PaperSubmissionDraft(
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
            resultSet.close();
            statement.close();
            connection.close();
            return Optional.of(draft);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch paper submission draft", e);
        }
    }

    @Override
    public void saveOrUpdate(PaperSubmissionDraft draft) {
        String updateSql = "UPDATE paper_submission_drafts SET title = ?, authors = ?, affiliations = ?, abstract_text = ?, keywords = ?, contact_details = ?, updated_at = ? WHERE author_email = ?";
        String insertSql = "INSERT INTO paper_submission_drafts (author_email, title, authors, affiliations, abstract_text, keywords, contact_details, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement update = connection.prepareStatement(updateSql);
            update.setString(1, draft.getTitle());
            update.setString(2, draft.getAuthors());
            update.setString(3, draft.getAffiliations());
            update.setString(4, draft.getPaperAbstract());
            update.setString(5, draft.getKeywords());
            update.setString(6, draft.getContactDetails());
            update.setTimestamp(7, Timestamp.from(draft.getUpdatedAt()));
            update.setString(8, draft.getAuthorEmail());
            int updatedRows = update.executeUpdate();
            if (updatedRows == 1) {
                update.close();
                connection.close();
                return;
            }
            PreparedStatement insert = connection.prepareStatement(insertSql);
            insert.setString(1, draft.getAuthorEmail());
            insert.setString(2, draft.getTitle());
            insert.setString(3, draft.getAuthors());
            insert.setString(4, draft.getAffiliations());
            insert.setString(5, draft.getPaperAbstract());
            insert.setString(6, draft.getKeywords());
            insert.setString(7, draft.getContactDetails());
            insert.setTimestamp(8, Timestamp.from(draft.getUpdatedAt()));
            insert.executeUpdate();
            insert.close();
            update.close();
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save paper submission draft", e);
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
}
