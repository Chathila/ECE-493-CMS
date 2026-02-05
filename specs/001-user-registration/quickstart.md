# Quickstart: Register User Account

## Goal

Validate the user registration flow for US-01.

## Preconditions

- Registration page is available.
- Email validation service is reachable.
- No existing account uses the test email.

## Happy Path

1. Open the registration page.
2. Enter a valid email address and a password that meets CMS policy.
3. Submit the form.
4. Confirm a success message is shown.
5. Confirm the user is redirected to the login page.

## Validation Errors

- Submit with a missing email or password and confirm the missing-field error.
- Submit with an invalid email format and confirm the invalid-email error.
- Submit with a password that does not meet CMS policy and confirm the password
  requirements are shown.
- Submit with an email that is already registered and confirm the unique-email
  error.

## Service Outage

- Simulate email validation service unavailability and confirm registration is
  blocked with a try-again-later error.
