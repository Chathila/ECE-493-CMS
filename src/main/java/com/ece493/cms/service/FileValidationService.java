package com.ece493.cms.service;

import com.ece493.cms.model.FileValidationResult;
import com.ece493.cms.model.ManuscriptFile;

public interface FileValidationService {
    FileValidationResult validate(String authorEmail, ManuscriptFile manuscriptFile);
}
