package com.ece493.cms.repository;

import java.util.List;

public interface RefereeAssignmentRepository {
    void saveAssignments(String paperId, List<String> refereeEmails);

    long countAssignmentsByRefereeEmail(String refereeEmail);

    long countAssignmentsByPaperId(String paperId);
}
