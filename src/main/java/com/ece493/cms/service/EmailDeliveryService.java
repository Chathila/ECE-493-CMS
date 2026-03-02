package com.ece493.cms.service;

public interface EmailDeliveryService {
    void send(String recipientEmail, String subject, String body);
}
