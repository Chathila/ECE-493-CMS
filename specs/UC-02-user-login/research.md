# Phase 0 Research: User Login

## Decision: Email is the login username

**Rationale**: The use case specifies "username (email)" and prior registration
flow aligns login with email.

**Alternatives considered**: Separate username field.

## Decision: Authentication service outage blocks login

**Rationale**: Credential validation is required to authenticate; if the auth
service is unavailable, login must fail with a clear error.

**Alternatives considered**: Cached credentials or automatic retries.

## Decision: No lockout or throttling in this feature

**Rationale**: Lockout policy is an open issue in the use case and not defined;
this feature only enforces inactive/locked status when present.

**Alternatives considered**: Lockout after N failed attempts or throttling.
