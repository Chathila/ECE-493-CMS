# Phase 0 Research: Register User Account

## Decision: Account activation is immediate

**Rationale**: SRS flow states the system stores the user and redirects to login
immediately after registration.

**Alternatives considered**: Email verification or admin approval gates.

## Decision: Email is the login username

**Rationale**: Registration uses email + password; SRS notes login uses
"username + password" which aligns with email as the username.

**Alternatives considered**: Separate username field captured at registration.

## Decision: Password policy is defined by CMS policy

**Rationale**: SRS does not specify complexity rules; the system validates against
an external CMS password policy.

**Alternatives considered**: Hard-coded minimum length or complexity.

## Decision: Email validation service outage blocks registration

**Rationale**: Email validity and uniqueness are mandatory gates for account
creation; allowing registration without validation risks invalid accounts.

**Alternatives considered**: Allow registration and validate later; automatic
retries before blocking.

## Decision: No rate limiting in this feature

**Rationale**: The use case does not specify throttling, and the feature scope is
limited to registration flow behavior.

**Alternatives considered**: Basic throttling on repeated failed attempts.
