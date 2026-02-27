# GLOBAL SHARED ARCHITECTURE

## Architectural Baseline

Proposed shared architecture for Lab 3 implementation is a layered MVC web app aligned with SRS object-oriented/three-layer constraints and spec plans. (Source: `CMS-SRS.pdf` §2.4; all `specs/UC-*/plan.md`)

- Presentation layer: controllers + HTML views + static assets
- Domain/application layer: use-case services and validators
- Persistence/integration layer: repositories + external service adapters
- Core model layer: domain entities and request/response DTOs

## Proposed Package Structure

```text
src/main/java/.../
  controller/
    auth/
    submission/
    review/
    decision/
    schedule/
    registration/
  service/
    auth/
    submission/
    review/
    decision/
    schedule/
    registration/
    shared/
  repository/
    auth/
    submission/
    review/
    decision/
    schedule/
    registration/
  model/
    auth/
    submission/
    review/
    decision/
    schedule/
    registration/
  dto/
  validation/
  security/
  integration/
    email/
    payment/
    file/
    scheduling/

src/main/resources/
  views/
  static/
  db/migration/
```

Rationale/source:
- Matches repeated MVC folder intent from tasks and plans (`src/controllers`, `src/services`, `src/models`, `src/views`, `src/static`). (Source: all `specs/UC-*/tasks.md`, `plan.md`)

## Controller-to-Route/Page Mapping (Shared)

- Auth:
  - `/register` -> registration controller + `register.html` (UC-01)
  - `/login` -> login controller + `login.html` (UC-02)
  - `/account/password` -> change-password controller + `change-password.html` (UC-03)
- Submission:
  - `/papers/submit`, `/papers/draft/save`, `/papers/file/validate` -> submit/draft/file controllers + `submit-paper.html` (UC-04/05/06)
- Review lifecycle:
  - `/papers/{paper_id}/referees/assign`
  - `/reviewers/{reviewer_id}/workload/check`
  - `/assignments/{assignment_id}/invitation/send`
  - `/invitations/{invitation_id}/response`
  - `/assignments/{assignment_id}/review-form`
  - `/assignments/{assignment_id}/reviews`
  (UC-07..UC-12)
- Decision lifecycle:
  - `/papers/{paper_id}/decision`
  - `/papers/{paper_id}/decision/notify`
  (UC-13/14)
- Scheduling:
  - `/schedule/generate`
  - `/schedule/{schedule_id}`
  - `/schedule/final` + `final-schedule.html`
  (UC-15/16/17)
- Registration/payment/ticketing:
  - `/registration/prices` + `registration-prices.html`
  - `/registration/payments`
  - `/payments/{payment_id}/ticket`
  - `/tickets/{ticket_id}` + `ticket-status.html`
  (UC-18/19/20)

Source: all OpenAPI contracts + relevant tasks/spec view references.

## DB Approach for Tests

### Embedded DB
- Use H2 in-memory for automated tests to ensure deterministic and isolated persistence behavior.
- Boot each integration test with a fresh schema and seed data for that UC scenario.

Why:
- All UCs assume relational persistence, and acceptance tests require DB side-effect validation. (Source: all `plan.md` storage context; `UC-*_tests.md` expected results)

### Schema Management
- Use migration scripts under `db/migration` as single source of schema truth.
- Apply migrations in tests before each suite/class.
- Maintain seed fixtures per UC domain area (auth/submission/review/schedule/registration).

Note:
- This is architectural proposal for future implementation; no migrations are added in this run.

## Shared Components (Cross-UC)

1. Password hashing/verification utility
- Needed by UC-01/02/03 and SRS security constraints.

2. Validation utilities
- Email, required fields, password policy adapter, file validation helpers, schedule conflict checks, payment field checks.

3. Error response conventions
- Standard envelope for API errors (message + optional structured fields/missing codes), with mapping to required HTTP statuses in contracts.

4. Session/auth handling
- Login session state, role checks (editor/referee/author/attendee/public), re-login enforcement after password change.

5. Notification abstraction
- Email + in-system channel for invitations, decisions, and tickets with failure logging hooks.

6. File upload/storage adapter
- Manuscript upload/validation and storage metadata persistence.

7. Payment gateway adapter
- Decline/unavailable/success handling, confirmation record handoff.

8. Retry-later/failure logging policy
- Shared mechanism for 5xx/503 class operational failures and user messaging.

Source: cross-UC FRs/contracts/data models (`UC-01`..`UC-20`), `CMS-SRS.pdf` §4 security + quality requirements.

## Conflicts / Ambiguities

- Domain rule divergence for reviewer count and file constraints should be resolved before shared validator implementation.
- Error payload standardization is inconsistent across contracts (`Error`, `ValidationError`, `MissingDataError`).
- Tasks in UC-09..20 reference missing contract README artifacts.

## Open Questions

- Should shared auth be session-cookie based, token-based, or both? (not specified)
- Should role authorization be centralized middleware/interceptor or per-controller checks?
- What is the canonical schema for audit/failure logs across invitation, decision, and ticket delivery failures?
- Should schedule publication be explicit state transition distinct from generation/editing, and where is that endpoint defined?
