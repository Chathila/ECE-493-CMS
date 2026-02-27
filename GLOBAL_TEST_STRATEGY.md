# GLOBAL TEST STRATEGY

## Purpose

Define one consistent automation strategy for UC-01..UC-20 acceptance requirements while preserving use-case fidelity and the repository’s test expectations. (Source: root `UC-01_tests.md`..`UC-20_tests.md`; all `specs/UC-*/plan.md` Testing notes; `CMS-SRS.pdf` §1.1 baseline/testing intent)

## Test Pyramid and Mapping

### 1) Unit Tests (Service/Domain Logic)
Focus: deterministic business rules and state transitions.

- Validation rules: required fields, format checks, status checks, policy checks.
  - Examples: login credential checks, password-change validations, file size/format validation, review completeness checks, schedule conflict detection, payment input validation.
- Decision and transition logic:
  - Invitation deduplication, single-response lock, assignment status transitions, retry-later path selection.
- Serialization/DTO mapping and small utility behavior.

Source mapping:
- Functional rule density is highest in UC specs FR sections and clarifications. (Source: all `specs/UC-*/spec.md`, `clarifications.md`)

### 2) Integration Tests (Controller + Persistence + External Adapters)
Focus: endpoint behavior + DB effects + adapter interactions.

- Contract-level status and payload checks for each OpenAPI endpoint.
- Persistence assertions:
  - e.g., account created/not created, review stored/not stored, decision/ticket/payment states.
- External dependency behavior via test doubles/fakes:
  - email/notification, payment service, file storage, scheduling algorithm.
- Failure-path integration checks required by acceptance suites:
  - 4xx/5xx/503/timeout-like paths and “retry later” messages.

Source mapping:
- Endpoints and response codes from contracts (`specs/UC-*/contracts/*.openapi.yaml`).
- DB effect expectations from acceptance tests (`UC-*_tests.md`) and data models.

### 3) Acceptance-Level Scenario Tests
Focus: executable versions of AT-01..AT-n in each `UC-XX_tests.md`.

- Build one scenario test per documented AT case.
- Drive via HTTP + rendered-page assertions (where applicable) against seeded test data.
- Keep one-to-one traceability table from AT IDs to test class/method names.

Source mapping:
- Root acceptance suite files `UC-01_tests.md`..`UC-20_tests.md`.

## UI-Only Step Handling

Automate:
- Functional UI assertions tied to requirements: correct page/view rendered, error/success message shown, redirect target, inline conflict/error indicators.

Map-but-don’t-over-automate:
- Pure presentation details that do not alter requirement outcomes (styling exactness, minor layout).
- For those, maintain a manual checklist linked to AT steps.

Rationale/source:
- Plans repeatedly indicate acceptance tests + manual UI validation. (Source: all `specs/UC-*/plan.md` Technical Context “Repository acceptance tests + manual UI validation”)

## Branch Coverage Enforcement (JaCoCo + Maven)

Target: 100% branch coverage for implemented code in Lab 3 scope.

Proposed enforcement pattern:
1. Use Maven Surefire for unit tests and Failsafe (or separate profile) for integration tests.
2. Add JaCoCo plugin with:
   - `prepare-agent` during test phases.
   - `report` for HTML/XML output.
   - `check` rule at `verify` with branch ratio `1.00` and line ratio `1.00` for selected packages.
3. Gate CI/local verification on `mvn verify` failing if thresholds are not met.
4. Exclude generated or non-domain boilerplate only if explicitly justified and documented.
5. Track per-UC coverage deltas as new features are implemented.

Note:
- This is strategy-only; no build file changes are made in this run.

## Traceability Model

For each UC:
- `FR` -> unit/integration test IDs
- `AT` -> acceptance scenario test IDs
- contract endpoint -> integration test IDs

Store a running matrix in `tests/` docs so every requirement has at least one automated assertion.

## Cross-Cutting Negative Test Catalog

Apply across most UCs (derived from acceptance suites/specs):
- missing required data
- invalid format/data
- duplicate/constraint violation
- unauthorized or invalid state transition
- external service unavailable
- database/storage failure
- retry guidance and no unintended persistence

(Sources: all `UC-*_tests.md`; all `specs/UC-*/spec.md` edge/error FRs)

## Conflicts / Ambiguities Affecting Testing

- Reviewer count rule conflict (SRS vs UC-07) changes expected outcomes for assignment tests.
- File format/size conflict (SRS vs UC-04/06) changes positive/negative file tests.
- Message content requirements vary in strictness across UCs (some minimal-content clarifications).

(Sources: `CMS-SRS.pdf` §3.3-§3.4; `UC-04/06/07 spec+clarifications`; `analysis.md` files)

## Open Questions

- Which baseline defines acceptance truth when SRS and UC specs conflict?
- Should 100% branch coverage apply to both unit+integration combined or per-module/package?
- Are any classes (DTOs, generated mappers) exempt from coverage checks?
