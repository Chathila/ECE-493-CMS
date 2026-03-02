package com.ece493.cms.service;

import java.util.List;
import java.util.Map;

public interface ReviewValidationService {
    List<String> validate(Map<String, String> responses);
}
