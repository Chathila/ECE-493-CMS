# Quickstart: Receive Review Invitation Email

## Goal

Validate invitation email sending, failure handling, and duplicate suppression
for UC-09.

## Preconditions

- Editor is logged in and can assign referees.
- Referee has a registered email address.
- Paper exists with title and abstract.

## Happy Path

1. Assign a referee to a paper.
2. Confirm an invitation email is sent containing paper title, abstract, and
   accept/reject instructions.

## Delivery Failure

- Simulate an email service outage and confirm:
  - A delivery failure record is created.
  - The editor is notified of the delivery issue.

## Invalid Email

- Use a referee with an invalid email address and confirm:
  - The system records the error.
  - The editor is alerted to update the referee contact information.

## Duplicate Assignment

- Assign the same referee to the same paper twice and confirm duplicate
  invitations are suppressed.
