# Quickstart: Generate Conference Schedule

## Goal

Validate schedule generation, missing data handling, and algorithm failure
logging for UC-15.

## Preconditions

- Administrator is logged in.
- Accepted papers are available.
- Rooms and time slots are available.

## Happy Path

1. Request schedule generation.
2. Confirm schedule is generated, stored, and displayed to the administrator.

## Missing Data

- Remove rooms or time slots and confirm missing-data error identifies the
  missing data type.

## Algorithm Failure

- Simulate algorithm failure and confirm error is shown and failure is logged.
