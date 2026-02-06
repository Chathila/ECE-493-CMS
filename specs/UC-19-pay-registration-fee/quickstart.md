# Quickstart: Pay Conference Registration Fee

## Goal

Validate payment processing, decline handling, invalid details errors, and
service unavailability messages for UC-19.

## Preconditions

- Attendee is logged in and has selected a registration type.
- Registration prices are available.

## Happy Path

1. Submit valid payment details.
2. Confirm payment is recorded and confirmation is shown.

## Invalid Payment Details

- Submit invalid or incomplete details and confirm correction message and no
  processing.

## Declined Payment

- Simulate a decline and confirm failure message with retry option.

## Service Unavailable

- Simulate service outage and confirm retry-later message.
