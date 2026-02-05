# Quickstart: Change User Password

## Goal

Validate the password change flow for UC-03.

## Preconditions

- User is logged in.
- Change password page is available.
- Authentication service is reachable.

## Happy Path

1. Open the change password page.
2. Enter current password, new password, and confirmation.
3. Submit the form.
4. Confirm success message is shown.
5. Confirm user is required to log in again.

## Validation Errors

- Submit with missing fields and confirm the missing-field error.
- Submit with incorrect current password and confirm the invalid-current-password error.
- Submit with a weak new password and confirm password guidelines are shown.
- Submit with mismatched new password confirmation and confirm mismatch error.

## Service Outage

- Simulate authentication service unavailability and confirm password change is
  blocked with a try-again-later error.
