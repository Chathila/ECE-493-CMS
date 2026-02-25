package cms.services;

import cms.models.RegistrationRequest;
import cms.models.RegistrationResult;

public interface RegistrationService {
    RegistrationResult register(RegistrationRequest request);
}
