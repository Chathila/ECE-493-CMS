# Quickstart: View Conference Registration Prices

## Goal

Validate registration price display, unavailable pricing messaging, and
retrieval failure handling for UC-18.

## Preconditions

- Pricing information exists (for happy path).
- CMS website is accessible to guests and registered users.

## Happy Path

1. Request registration prices.
2. Confirm prices display by attendee category.

## Pricing Unavailable

- Request prices when pricing is not available and confirm unavailable message.

## Retrieval Failure

- Simulate database retrieval failure and confirm retry-later message and no
  prices displayed.
