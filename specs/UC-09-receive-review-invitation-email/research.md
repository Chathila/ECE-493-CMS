# Phase 0 Research: Receive Review Invitation Email

## Decision: No automatic retries for failed invitation sends

**Rationale**: Use case specifies logging and notifying the editor on failure but
no retry behavior; avoid inventing retry policies.

**Alternatives considered**: Automatic retries with backoff; editor-triggered
manual resend.

## Decision: Invalid referee email triggers editor notification

**Rationale**: Use case extension requires alerting the editor to update contact
information.

**Alternatives considered**: Log only; disable referee account.

## Decision: Suppress duplicate invitations for the same referee-paper pair

**Rationale**: Prevents duplicate emails and aligns with requirement to avoid
sending duplicate invitations.

**Alternatives considered**: Allow duplicates; replace and resend latest invite.
