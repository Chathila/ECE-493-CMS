package com.ece493.cms.model;

public class PaymentProcessingResult {
    private final int statusCode;
    private final String message;
    private final String paymentId;
    private final String status;

    private PaymentProcessingResult(int statusCode, String message, String paymentId, String status) {
        this.statusCode = statusCode;
        this.message = message;
        this.paymentId = paymentId;
        this.status = status;
    }

    public static PaymentProcessingResult success(String paymentId) {
        return new PaymentProcessingResult(200, "Payment was successful.", paymentId, "confirmed");
    }

    public static PaymentProcessingResult invalidDetails() {
        return new PaymentProcessingResult(400, "Please correct payment details and try again.", null, null);
    }

    public static PaymentProcessingResult declined() {
        return new PaymentProcessingResult(
                402,
                "Payment was declined. Please retry with corrected or new payment details.",
                null,
                null
        );
    }

    public static PaymentProcessingResult unavailable() {
        return new PaymentProcessingResult(503, "Payment service unavailable; please try again later.", null, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getStatus() {
        return status;
    }
}
