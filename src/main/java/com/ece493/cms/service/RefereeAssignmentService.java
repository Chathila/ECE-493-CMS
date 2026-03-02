package com.ece493.cms.service;

import com.ece493.cms.model.RefereeAssignmentResult;

import java.util.List;

public interface RefereeAssignmentService {
    RefereeAssignmentResult assignReferees(String editorEmail, String paperId, List<String> refereeEmails);
}
