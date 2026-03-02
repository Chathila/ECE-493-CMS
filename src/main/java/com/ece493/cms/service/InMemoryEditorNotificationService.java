package com.ece493.cms.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryEditorNotificationService implements EditorNotificationService {
    public static final class EditorNotification {
        private final String editorEmail;
        private final String message;

        public EditorNotification(String editorEmail, String message) {
            this.editorEmail = editorEmail;
            this.message = message;
        }

        public String getEditorEmail() {
            return editorEmail;
        }

        public String getMessage() {
            return message;
        }
    }

    private final List<EditorNotification> notifications = new ArrayList<>();

    @Override
    public void notifyEditor(String editorEmail, String message) {
        notifications.add(new EditorNotification(editorEmail, message));
    }

    public List<EditorNotification> notifications() {
        return Collections.unmodifiableList(notifications);
    }
}
