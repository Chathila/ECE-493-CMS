## Specification Analysis Report

| ID | Category | Severity | Location(s) | Summary | Recommendation |
|----|----------|----------|-------------|---------|----------------|
| U1 | Underspecification | MEDIUM | `spec.md` Edge Cases | Edge cases list includes open questions (assignment page unavailability) without requirements. | Add explicit requirements or mark as out of scope with rationale. |
| U2 | Underspecification | MEDIUM | `tasks.md` Phases 3–5 | Tasks implement the flow but don’t explicitly reference or validate the OpenAPI contract in `contracts/assign-referees.openapi.yaml`. | Add a task to align implementation with contract or explicitly note it as reference-only. |
| A1 | Ambiguity | MEDIUM | `spec.md` FR‑009..FR‑011 | Error message content lacks minimum expectations. | Define minimum error content or link to policy/UX guidelines. |
| C1 | Consistency | LOW | `spec.md` Edge Cases | Edge case bullets include resolved items mixed with open questions; phrasing is inconsistent. | Normalize formatting and reword resolved items as statements. |

## Coverage Summary Table

| Requirement Key | Has Task? | Task IDs | Notes |
|-----------------|-----------|----------|-------|
| assign-option | Yes | T007, T009 | Controller + route |
| assignment-interface | Yes | T008 | UI |
| validate-referee-email | Yes | T014 | Service |
| enforce-workload | Yes | T016 | Service |
| require-minimum-referees | Yes | T021 | UI helper text |
| assign-referees | Yes | T010, T011 | Service + model |
| store-assignments | Yes | T011 | Model stub |
| send-invitations | Yes | T012 | Notification service |
| invalid-email-error | Yes | T015 | UI |
| workload-violation-error | Yes | T018 | UI |
| referee-limit-error | N/A | — | No maximum is enforced per clarifications |
| notification-warning | Yes | T017, T019 | Service + UI |

## Constitution Alignment Issues

- None detected.

## Unmapped Tasks

- None.

## Metrics

- Total Requirements: 12
- Total Tasks: 21
- Coverage %: 100%
- Ambiguity Count: 1
- Duplication Count: 0
- Critical Issues Count: 0

## Next Actions

- You can proceed to `/speckit.implement`.
- If you want to tighten the spec, resolve the edge case questions and define minimum error message content.
