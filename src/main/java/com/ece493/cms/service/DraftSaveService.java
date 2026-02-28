package com.ece493.cms.service;

import com.ece493.cms.model.DraftSaveRequest;
import com.ece493.cms.model.DraftSaveResult;

public interface DraftSaveService {
    DraftSaveResult saveDraft(String authorEmail, DraftSaveRequest request);
}
