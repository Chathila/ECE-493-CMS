package com.ece493.cms.service;

import com.ece493.cms.model.ReviewerWorkload;

public interface WorkloadCheckService {
    ReviewerWorkload getReviewerWorkload(String reviewerEmail);
}
