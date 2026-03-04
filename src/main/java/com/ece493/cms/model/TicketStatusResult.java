package com.ece493.cms.model;

public class TicketStatusResult {
    private final int statusCode;
    private final String message;
    private final String ticketId;
    private final String status;

    private TicketStatusResult(int statusCode, String message, String ticketId, String status) {
        this.statusCode = statusCode;
        this.message = message;
        this.ticketId = ticketId;
        this.status = status;
    }

    public static TicketStatusResult delivered(String ticketId) {
        return new TicketStatusResult(201, "Ticket generated and delivered.", ticketId, "delivered");
    }

    public static TicketStatusResult failed(String ticketId) {
        return new TicketStatusResult(500, "Delivery failed; the ticket is available in the CMS.", ticketId, "failed");
    }

    public static TicketStatusResult error(int statusCode, String message) {
        return new TicketStatusResult(statusCode, message, null, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getStatus() {
        return status;
    }
}
