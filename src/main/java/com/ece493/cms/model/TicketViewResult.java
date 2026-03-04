package com.ece493.cms.model;

public class TicketViewResult {
    private final int statusCode;
    private final String message;
    private final String ticketId;
    private final String content;

    private TicketViewResult(int statusCode, String message, String ticketId, String content) {
        this.statusCode = statusCode;
        this.message = message;
        this.ticketId = ticketId;
        this.content = content;
    }

    public static TicketViewResult found(ConfirmationTicket ticket) {
        return new TicketViewResult(200, "Ticket retrieved.", ticket.getTicketId(), ticket.getContent());
    }

    public static TicketViewResult error(int statusCode, String message) {
        return new TicketViewResult(statusCode, message, null, null);
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

    public String getContent() {
        return content;
    }
}
