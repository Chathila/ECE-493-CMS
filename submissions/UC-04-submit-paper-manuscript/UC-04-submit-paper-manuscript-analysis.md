## Specification Analysis Report

| ID | Category | Severity | Location(s) | Summary | Recommendation |
|----|----------|----------|-------------|---------|----------------|
| U1 | Underspecification | MEDIUM | `spec.md` Edge Cases | Edge cases list includes open questions (abandonment, submission page unavailability) without requirements. | Add explicit requirements or mark as out of scope with rationale. |
| U2 | Underspecification | MEDIUM | `tasks.md` Phases 3–5 | Tasks implement the flow but don’t explicitly reference or validate the OpenAPI contract in `contracts/submit-paper.openapi.yaml`. | Add a task to align implementation with contract or explicitly note it as reference-only. |
| A1 | Ambiguity | MEDIUM | `spec.md` FR‑006/FR‑013 | "invalid metadata" and error message content lack minimum expectations. | Define minimum error content or link to policy/UX guidelines. |
| C1 | Consistency | LOW | `spec.md` Edge Cases | Edge case bullets include resolved items mixed with open questions; phrasing is inconsistent. | Normalize formatting and reword resolved items as statements. |

## Coverage Summary Table

| Requirement Key | Has Task? | Task IDs | Notes |
|-----------------|-----------|----------|-------|
| submit-paper-option | Yes | T008, T010 | Controller + route |
| submission-form | Yes | T009 | Form view |
| require-metadata-fields | Yes | T015, T016 | Validation service |
| upload-manuscript | Yes | T011, T013 | Service + model |
| validate-required-fields | Yes | T015 | Validation |
| validate-metadata-format | Yes | T016 | Validation |
| validate-file-format | Yes | T019 | File storage service |
| validate-file-size | Yes | T020 | File storage service |
| store-metadata | Yes | T012 | Model stub |
| store-file | Yes | T013 | Model stub |
| confirmation-message | Yes | T014, T025 | UI + wording |
| redirect-dashboard | Yes | T014 | UI redirect |
| error-metadata | Yes | T017, T018 | Service + UI |
| error-unsupported-format | Yes | T022 | UI |
| error-size-limit | Yes | T023 | UI |
| error-upload-failure | Yes | T021, T024, T027 | Service + UI |

## Constitution Alignment Issues

- None detected.

## Unmapped Tasks

- None.

## Metrics

- Total Requirements: 16
- Total Tasks: 27
- Coverage %: 100%
- Ambiguity Count: 1
- Duplication Count: 0
- Critical Issues Count: 0

## Next Actions

- You can proceed to `/speckit.implement`.
- If you want to tighten the spec, resolve the edge case questions and define minimum error message content.
