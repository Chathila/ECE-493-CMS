# Quickstart: Enforce Reviewer Workload Limit

## Goal

Validate workload limit checks for UC-08.

## Preconditions

- Editor is logged in.
- Reviewer exists with workload data.

## Happy Path

1. Attempt to assign a reviewer with assigned_count < workload_limit.
2. Confirm assignment proceeds and count updates.

## Limit Reached

- Attempt to assign a reviewer at the limit and confirm workload violation error.

## Retrieval Failure

- Simulate workload retrieval failure and confirm assignment blocked with error.
