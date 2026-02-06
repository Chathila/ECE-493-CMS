# Quickstart: Accept or Reject Review Invitation

## Goal

Validate invitation response submission, assignment updates, and error handling
for UC-10.

## Preconditions

- Referee has a valid, open invitation.
- Paper exists with title and abstract.
- Editor can receive response notifications.

## Happy Path

1. Open a valid invitation.
2. Submit Accept and confirm response is recorded and assignment status updated.
3. Submit Reject on a different invitation and confirm status updated.

## Expired Invitation

- Attempt to open an expired invitation and confirm an expired message is shown.

## Database Error

- Simulate a database failure on response submission and confirm an error is
  shown and the response is not recorded.
