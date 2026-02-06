# Quickstart: View Final Conference Schedule

## Goal

Validate final schedule display, unpublished schedule messaging, and retrieval
failure handling for UC-17.

## Preconditions

- Final schedule is published (for happy path).
- Users can access the CMS website.

## Happy Path

1. Request the final schedule.
2. Confirm schedule displays with session times, locations, and titles.

## Not Published

- Request schedule before publication and confirm "not published yet" message.

## Retrieval Failure

- Simulate database retrieval failure and confirm retry-later message and no
  schedule display.
