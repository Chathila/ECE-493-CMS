# Quickstart: Access Paper Review Form

## Goal

Validate authorized access to review forms, access-denied handling, and form
unavailability errors for UC-11.

## Preconditions

- Referee is logged in and has accepted the review invitation.
- Review assignment exists with accepted status.
- Review form and paper details are available.

## Happy Path

1. Select an assigned paper from the referee dashboard.
2. Confirm the review form and paper details display.

## Access Denied

- Attempt to access a paper not assigned or not accepted and confirm an
  access-denied message is shown and the form does not load.

## Form Unavailable

- Simulate review form retrieval failure and confirm a "form unavailable"
  message is shown and the form does not load.
