package com.ece493.cms.service;

import com.ece493.cms.model.FinalDecision;
import com.ece493.cms.model.NotificationDeliveryFailure;

import java.time.Instant;

public class FinalDecisionNotificationService {
    private final EmailDeliveryService emailDeliveryService;
    private final NotificationFailureRepository notificationFailureRepository;
    private final EditorNotificationService editorNotificationService;

    public FinalDecisionNotificationService(
            EmailDeliveryService emailDeliveryService,
            NotificationFailureRepository notificationFailureRepository,
            EditorNotificationService editorNotificationService
    ) {
        this.emailDeliveryService = emailDeliveryService;
        this.notificationFailureRepository = notificationFailureRepository;
        this.editorNotificationService = editorNotificationService;
    }

    public boolean dispatch(FinalDecision decision) {
        try {
            emailDeliveryService.send(
                    decision.getAuthorEmail(),
                    "Final Paper Decision",
                    decision.getDecision()
            );
            return true;
        } catch (IllegalStateException e) {
            notificationFailureRepository.save(new NotificationDeliveryFailure(
                    0L,
                    decision.getDecisionId(),
                    "email",
                    Instant.now(),
                    e.getMessage() == null ? "delivery failed" : e.getMessage()
            ));
            editorNotificationService.notifyEditor(decision.getEditorEmail(), "delivery failed");
            return false;
        }
    }
}
