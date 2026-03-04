package com.ece493.cms.service;

import com.ece493.cms.model.ConfirmationTicket;
import com.ece493.cms.model.Payment;
import com.ece493.cms.model.TicketDeliveryFailure;
import com.ece493.cms.model.TicketStatusResult;
import com.ece493.cms.model.TicketViewResult;

import java.time.Instant;
import java.util.Optional;

public class TicketService {
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final TicketFailureRepository ticketFailureRepository;
    private final TicketDeliveryService ticketDeliveryService;
    private long failureSequence = 1L;

    public TicketService(
            PaymentRepository paymentRepository,
            TicketRepository ticketRepository,
            TicketFailureRepository ticketFailureRepository,
            TicketDeliveryService ticketDeliveryService
    ) {
        this.paymentRepository = paymentRepository;
        this.ticketRepository = ticketRepository;
        this.ticketFailureRepository = ticketFailureRepository;
        this.ticketDeliveryService = ticketDeliveryService;
    }

    public TicketStatusResult generateAndDeliver(String paymentId) {
        if (isBlank(paymentId)) {
            return TicketStatusResult.error(400, "Payment id is required.");
        }
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        if (paymentOptional.isEmpty()) {
            return TicketStatusResult.error(404, "Payment confirmation not found.");
        }
        Payment payment = paymentOptional.get();

        ConfirmationTicket ticket;
        try {
            ticket = ticketRepository.save(
                    payment.getAttendeeEmail(),
                    payment.getPaymentId(),
                    buildTicketContent(payment)
            );
        } catch (IllegalStateException e) {
            return TicketStatusResult.error(500, "Ticket could not be generated.");
        }

        TicketDeliveryAttemptResult deliveryResult = ticketDeliveryService.deliver(ticket);
        if (deliveryResult.isEmailDelivered() && deliveryResult.isInSystemDelivered()) {
            return TicketStatusResult.delivered(ticket.getTicketId());
        }

        if (!deliveryResult.isEmailDelivered()) {
            ticketFailureRepository.save(new TicketDeliveryFailure(
                    String.valueOf(failureSequence++),
                    ticket.getTicketId(),
                    "email",
                    Instant.now(),
                    "Email delivery failed"
            ));
        }
        if (!deliveryResult.isInSystemDelivered()) {
            ticketFailureRepository.save(new TicketDeliveryFailure(
                    String.valueOf(failureSequence++),
                    ticket.getTicketId(),
                    "in-system",
                    Instant.now(),
                    "In-system delivery failed"
            ));
        }
        return TicketStatusResult.failed(ticket.getTicketId());
    }

    public TicketViewResult viewTicket(String ticketId) {
        if (isBlank(ticketId)) {
            return TicketViewResult.error(400, "Ticket id is required.");
        }
        final Optional<ConfirmationTicket> ticketOptional;
        try {
            ticketOptional = ticketRepository.findById(ticketId);
        } catch (IllegalStateException e) {
            return TicketViewResult.error(500, "Unable to retrieve ticket; please try again later.");
        }
        if (ticketOptional.isEmpty()) {
            return TicketViewResult.error(404, "Ticket not found.");
        }
        return TicketViewResult.found(ticketOptional.get());
    }

    private String buildTicketContent(Payment payment) {
        return "Registration payment confirmed. Payment ID: " + payment.getPaymentId()
                + ", Registration Type: " + payment.getRegistrationType();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
