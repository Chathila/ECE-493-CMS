# Quickstart: Make Final Paper Decision

## Goal

Validate final decision submission, incomplete review blocking, and database
error handling for UC-13.

## Preconditions

- Editor is logged in.
- Paper has all required reviews completed.
- Review period is closed.

## Happy Path

1. Select a paper awaiting decision.
2. Submit Accept or Reject.
3. Confirm decision is recorded and author notified.

## Reviews Incomplete

- Attempt decision with incomplete reviews and confirm a reviews-incomplete
  error is shown on the decision page.

## Database Error

- Simulate a database error and confirm a retry-later message is shown and
  decision is not recorded.
