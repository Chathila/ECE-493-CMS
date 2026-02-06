# Quickstart: Assign Referees to Submitted Papers

## Goal

Validate referee assignment flow for UC-07.

## Preconditions

- Editor is logged in.
- Submitted paper exists.
- Referee list is available.

## Happy Path

1. Open referee assignment interface for a paper.
2. Enter valid referee emails (at least one).
3. Submit assignment.
4. Confirm assignments stored and invitations sent.

## Validation Errors

- Submit with an invalid referee email and confirm error.
- Submit with a referee who exceeds workload and confirm workload error.
- Submit with fewer than one referee and confirm error.

## Notification Failure

- Simulate notification service outage and confirm warning while assignments are stored.
