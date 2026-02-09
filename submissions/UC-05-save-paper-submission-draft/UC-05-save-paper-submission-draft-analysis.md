## Specification Analysis Report

| ID | Category | Severity | Location(s) | Summary | Recommendation |
|----|----------|----------|-------------|---------|----------------|
| U1 | Underspecification | MEDIUM | `spec.md` Edge Cases | Edge cases list includes open questions (form unavailability) without requirements. | Add explicit requirements or mark as out of scope with rationale. |
| U2 | Underspecification | MEDIUM | `tasks.md` Phases 3–5 | Tasks implement the flow but don’t explicitly reference or validate the OpenAPI contract in `contracts/save-draft.openapi.yaml`. | Add a task to align implementation with contract or explicitly note it as reference-only. |
| A1 | Ambiguity | MEDIUM | `spec.md` FR‑005/FR‑006 | Error message content and "draft validation rules" lack minimum expectations. | Define minimum error content or link to policy/UX guidelines. |
| C1 | Consistency | LOW | `spec.md` Edge Cases | Edge case bullets include resolved items mixed with open questions; phrasing is inconsistent. | Normalize formatting and reword resolved items as statements. |

## Coverage Summary Table

| Requirement Key | Has Task? | Task IDs | Notes |
|-----------------|-----------|----------|-------|
| save-option | Yes | T006, T008 | Controller + route |
| validate-draft-rules | Yes | T012 | Validation service |
| store-draft | Yes | T009, T010 | Service + model |
| confirmation-message | Yes | T011, T019 | UI + wording |
| validation-error | Yes | T013, T014 | Service + UI |
| storage-error | Yes | T015, T017 | Service + UI |
| no-change-save | Yes | T016, T018 | Service + UI |
| min-required-fields | Yes | T012, T020 | Validation + UI helper |

## Constitution Alignment Issues

- None detected.

## Unmapped Tasks

- None.

## Metrics

- Total Requirements: 7
- Total Tasks: 20
- Coverage %: 100%
- Ambiguity Count: 1
- Duplication Count: 0
- Critical Issues Count: 0

## Next Actions

- You can proceed to `/speckit.implement`.
- If you want to tighten the spec, resolve the edge case questions and define minimum error message content.
