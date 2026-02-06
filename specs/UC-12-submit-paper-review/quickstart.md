# Quickstart: Submit Paper Review

## Goal

Validate review submission, field-level validation errors, and storage error
handling for UC-12.

## Preconditions

- Referee is logged in and has accepted the review invitation.
- Review form is completed with required fields.
- Review assignment exists and is accepted.

## Happy Path

1. Submit a completed review.
2. Confirm review is stored and editor is notified.

## Validation Errors

- Submit with missing or invalid required fields and confirm field-level error
  messages are shown and review is not stored.

## Storage Error

- Simulate a storage failure and confirm a retry-later message is shown and
  review is not stored.
