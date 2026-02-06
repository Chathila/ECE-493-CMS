# Phase 0 Research: Enforce Reviewer Workload Limit

## Decision: Workload limit is configurable (default 5)

**Rationale**: Use case notes limit may be configurable in future; keep default 5.

**Alternatives considered**: Hard-code limit to 5.

## Decision: Missing workload data blocks assignment

**Rationale**: Cannot verify limit without data, so assignment must be blocked.

**Alternatives considered**: Treat missing workload as zero.

## Decision: Workload check failures block assignment

**Rationale**: Prevents exceeding limits when validation fails.

**Alternatives considered**: Allow assignment and reconcile later.
