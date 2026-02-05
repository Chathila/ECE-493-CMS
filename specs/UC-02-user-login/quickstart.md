# Quickstart: User Login

## Goal

Validate the user login flow for UC-02.

## Preconditions

- Login page is available.
- Authentication service is reachable.
- A registered user account exists.

## Happy Path

1. Open the login page.
2. Enter a valid email address and password.
3. Submit the form.
4. Confirm authentication success.
5. Confirm redirect to authorized home page.

## Validation Errors

- Submit with a missing email or password and confirm the missing-field error.
- Submit with an incorrect email or password and confirm the authentication
  failure message.
- Submit with an inactive or locked account and confirm the account status
  message.

## Service Outage

- Simulate authentication service unavailability and confirm login is blocked
  with a try-again-later error.
