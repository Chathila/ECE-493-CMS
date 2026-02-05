# Quickstart: Save Paper Submission Draft

## Goal

Validate the draft save flow for UC-05.

## Preconditions

- Author is logged in.
- Submission form is available.

## Happy Path

1. Open the submission form.
2. Enter a title and corresponding author email.
3. Select Save.
4. Confirm draft saved message appears.

## Validation Errors

- Attempt to save without a title or corresponding author email and confirm
  validation error.

## Storage Errors

- Simulate a database/storage failure and confirm the draft save error message.

## No-Change Save

- Select Save without changes and confirm success message.
