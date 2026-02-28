package com.ece493.cms.service;

import com.ece493.cms.model.DraftSaveRequest;

public interface DraftValidationService {
    String validate(DraftSaveRequest request);
}
