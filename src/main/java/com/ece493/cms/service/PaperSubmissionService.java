package com.ece493.cms.service;

import com.ece493.cms.model.PaperSubmissionRequest;
import com.ece493.cms.model.PaperSubmissionResult;

public interface PaperSubmissionService {
    PaperSubmissionResult submit(String authorEmail, PaperSubmissionRequest request);
}
