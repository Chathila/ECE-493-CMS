# Quickstart: Receive Registration Payment Confirmation Ticket

## Goal

Validate ticket generation, delivery, delivery failure handling, and CMS access
for UC-20.

## Preconditions

- Payment confirmation exists.
- Attendee has valid contact information.

## Happy Path

1. Trigger ticket generation after payment confirmation.
2. Confirm ticket is stored and delivered via email and in-system notification.

## Delivery Failure

- Simulate delivery failure and confirm:
  - Failure is logged.
  - Attendee is informed that the ticket is available in the CMS.

## Access Before Delivery

- Access the ticket in the CMS before delivery and confirm both view and
  download are available.
