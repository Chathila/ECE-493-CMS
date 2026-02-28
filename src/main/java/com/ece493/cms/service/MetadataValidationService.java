package com.ece493.cms.service;

import com.ece493.cms.model.MetadataValidationResult;
import com.ece493.cms.model.PaperSubmissionRequest;

public interface MetadataValidationService {
    MetadataValidationResult validate(PaperSubmissionRequest request);
}
