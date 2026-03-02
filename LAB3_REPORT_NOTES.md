# Lab 3 Report Notes (This Run)

## Scope Completed

- Bootstrapped Maven Java MVC project.
- Implemented UC-01 only (registration flow) from:
  - `specs/UC-01-register-user-account/spec.md`
  - `specs/UC-01-register-user-account/plan.md`
  - `specs/UC-01-register-user-account/tasks.md`
  - `specs/UC-01-register-user-account/contracts/registration.openapi.yaml`
- Added unit + integration + acceptance tests for UC-01.
- Enforced JaCoCo branch coverage threshold at 100% in Maven `verify`.

## Commands Run

1. `mvn test`
2. `mvn verify`

Both commands succeed in the final state.

## Coverage

- JaCoCo report directory:
  - `target/site/jacoco/index.html`
- JaCoCo branch gate configured in `pom.xml`:
  - `jacoco-maven-plugin` `check` execution
  - Branch `COVEREDRATIO` minimum: `1.00`

## Notes on Embedded Web-Layer Integration Testing

- Integration/acceptance tests run HTTP-like request/response flows through the servlet controller layer in-process.
- Reason: sandbox blocks opening listening sockets (`Operation not permitted`), so tests use embedded in-process servlet invocation with request/response wrappers while still verifying controller behavior and DB effects.

## Spec-Kit Command Note

- `/speckit.implement` CLI command is not available in this environment (`speckit` command not found), so UC-01 was implemented directly from the existing spec-kit artifacts listed above.

---

# Lab 3 Report Notes (UC-02 Run)

## Scope Completed

- Implemented UC-02 user login flow from:
  - `specs/UC-02-user-login/spec.md`
  - `specs/UC-02-user-login/plan.md`
  - `specs/UC-02-user-login/tasks.md`
  - `specs/UC-02-user-login/contracts/login.openapi.yaml`
  - `UC-02_tests.md`
- Added `/login` GET/POST endpoint behavior with:
  - missing required fields handling
  - incorrect credentials handling
  - inactive/locked account handling
  - authentication service unavailable handling
  - redirect on successful authentication to authorized home page
- Added/updated UI pages:
  - public homepage with Log In option
  - login form page
  - authorized home page
- Added unit + integration + acceptance tests and UC-02 traceability.

## Commands Run

1. `mvn test`
2. `mvn verify`

Both commands succeed in the final UC-02 state.

## Coverage

- JaCoCo report directory:
  - `target/site/jacoco/index.html`
- Final branch coverage:
  - `BRANCH missed=0 covered=88 ratio=100.00%`

## Traceability

- `traceability/UC-02-traceability.md` maps AT-01..AT-04 to automated tests.

## Spec-Kit Command Note

- `/speckit.implement` CLI command is not available in this environment (`/speckit.implement: no such file or directory`), so UC-02 was implemented directly from existing spec-kit artifacts.

---

# Lab 3 Report Notes (UC-03 Run)

## Scope Completed

- Implemented UC-03 change-user-password flow from:
  - `specs/UC-03-change-user-password/spec.md`
  - `specs/UC-03-change-user-password/plan.md`
  - `specs/UC-03-change-user-password/tasks.md`
  - `specs/UC-03-change-user-password/contracts/change-password.openapi.yaml`
  - `UC-03_tests.md`
- Added `/account/password` GET/POST endpoint behavior with:
  - required field validation
  - current password verification
  - new password policy enforcement
  - new password/confirmation match validation
  - authentication service unavailable handling
  - success confirmation + session invalidation (re-login required)
- Added/updated session flow so successful login stores user context used by UC-03.
- Added/updated UI pages:
  - change password form page
  - authorized home page includes change password option
- Added unit + integration + acceptance tests and UC-03 traceability.

## Commands Run

1. `mvn test`
2. `mvn verify`

Both commands succeed in the final UC-03 state.

## Coverage

- JaCoCo report directory:
  - `target/site/jacoco/index.html`
- Final branch coverage:
  - `BRANCH missed=0 covered=132 ratio=100.00%`

## Traceability

- `traceability/UC-03-traceability.md` maps AT-01..AT-04 to automated tests.

## Prompt/Spec-Kit Notes

- `prompts/implementation.txt` does not exist in this repository; UC-03 run was executed using `prompts/implementation-rest.txt`.
- `/speckit.implement` CLI command is not available in this environment (`/speckit.implement: no such file or directory`), so UC-03 was implemented directly from existing spec-kit artifacts.

---

# Lab 3 Report Notes (UC-04 Run)

## Scope Completed

- Implemented UC-04 submit-paper-manuscript flow from:
  - `specs/UC-04-submit-paper-manuscript/spec.md`
  - `specs/UC-04-submit-paper-manuscript/plan.md`
  - `specs/UC-04-submit-paper-manuscript/tasks.md`
  - `specs/UC-04-submit-paper-manuscript/contracts/submit-paper.openapi.yaml`
  - `UC-04_tests.md`
- Added `/papers/submit` GET/POST endpoint behavior with:
  - required metadata validation
  - metadata formatting validation
  - manuscript file format validation (PDF/DOCX)
  - manuscript file size validation (max 20 MB)
  - upload/storage failure handling with retry message
  - success confirmation + dashboard redirect payload
- Added database persistence for paper metadata (`paper_submissions` table).
- Added file storage service implementation for manuscript upload simulation.
- Added/updated UI pages:
  - submit paper form page
  - dashboard/home includes Submit Paper option
- Added unit + integration + acceptance tests and UC-04 traceability.

## Commands Run

1. `mvn test`
2. `mvn verify`

Both commands succeed in the final UC-04 state.

## Coverage

- JaCoCo report directory:
  - `target/site/jacoco/index.html`
- Final branch coverage:
  - `BRANCH missed=0 covered=224 ratio=100.00%`

## Traceability

- `traceability/UC-04-traceability.md` maps AT-01..AT-05 to automated tests.

## Spec-Kit Command Note

- `/speckit.implement` CLI command is not available in this environment (`/speckit.implement: no such file or directory`), so UC-04 was implemented directly from existing spec-kit artifacts.

---

# Lab 3 Report Notes (UC-05 Run)

## Scope Completed

- Implemented UC-05 save-paper-submission-draft flow from:
  - `specs/UC-05-save-paper-submission-draft/spec.md`
  - `specs/UC-05-save-paper-submission-draft/plan.md`
  - `specs/UC-05-save-paper-submission-draft/tasks.md`
  - `specs/UC-05-save-paper-submission-draft/contracts/save-draft.openapi.yaml`
  - `UC-05_tests.md`
- Added `/papers/draft/save` POST endpoint behavior with:
  - required draft fields validation (title + corresponding author email)
  - invalid character validation for draft fields
  - persistence of draft state in database
  - storage/database failure handling
  - no-change save detection with confirmation message
- Added database persistence for drafts (`paper_submission_drafts` table).
- Updated submit paper UI to include `Save Draft` action and helper text for minimum required draft fields.
- Added unit + integration + acceptance tests and UC-05 traceability.

## Commands Run

1. `mvn test`
2. `mvn verify`

Both commands succeed in the final UC-05 state.

## Coverage

- JaCoCo report directory:
  - `target/site/jacoco/index.html`
- Final branch coverage:
  - `BRANCH missed=0 covered=308 ratio=100.00%`

## Traceability

- `traceability/UC-05-traceability.md` maps AT-01..AT-03 to automated tests.

## Spec-Kit Command Note

- `/speckit.implement` CLI command is not available in this environment (`/speckit.implement: no such file or directory`), so UC-05 was implemented directly from existing spec-kit artifacts.

---

# Lab 3 Report Notes (UC-06 Run)

## Scope Completed

- Implemented UC-06 validate-paper-file flow from:
  - `specs/UC-06-validate-paper-file/spec.md`
  - `specs/UC-06-validate-paper-file/plan.md`
  - `specs/UC-06-validate-paper-file/tasks.md`
  - `specs/UC-06-validate-paper-file/contracts/validate-file.openapi.yaml`
  - `UC-06_tests.md`
- Added `/papers/file/validate` POST endpoint behavior with:
  - login/session precondition handling
  - supported format validation (PDF/DOCX)
  - max file size validation (20 MB)
  - corrupted/unreadable file handling (unprocessable file)
  - upload/system unavailable retry handling
  - success confirmation response
- Updated submit-paper UI flow to call file validation endpoint before final submit and show returned validation errors.
- Added unit + integration + acceptance tests and UC-06 traceability.

## Commands Run

1. `/speckit.implement` (not available in this environment)
2. `mvn test`
3. `mvn verify`

`mvn test` and `mvn verify` succeed in the final UC-06 state.

## Coverage

- JaCoCo report directory:
  - `target/site/jacoco/index.html`
- Final branch coverage:
  - `BRANCH missed=0 covered=390 ratio=100.00%`

## Traceability

- `traceability/UC-06-traceability.md` maps AT-01..AT-04 to automated tests.

## Spec-Kit Command Note

- `/speckit.implement` CLI command is not available in this environment (`/speckit.implement: no such file or directory`), so UC-06 was implemented directly from existing spec-kit artifacts.

---

# Lab 3 Report Notes (UC-07 Run)

## Scope Completed

- Ran coverage prompt (`prompts/test-coverage.txt`) before UC-07 implementation:
  - `mvn clean verify`
  - Branch coverage confirmed at 100%.
- Implemented UC-07 assign-referees flow from:
  - `specs/UC-07-assign-referees/spec.md`
  - `specs/UC-07-assign-referees/plan.md`
  - `specs/UC-07-assign-referees/tasks.md`
  - `specs/UC-07-assign-referees/contracts/assign-referees.openapi.yaml`
  - `UC-07_tests.md`
- Added `/papers/{paper_id}/referees/assign` flow with:
  - assignment interface page (`assign-referees.html`)
  - referee email existence validation
  - minimum one referee validation
  - workload limit enforcement (default 5 from CMS policy/UC-08 clarification)
  - assignment persistence in DB (`referee_assignments`)
  - invitation sending hook and warning behavior when notifications fail
- Added unit + integration + acceptance tests and UC-07 traceability.

## Commands Run

1. `mvn clean verify`
2. `/speckit.implement` (not available in this environment)
3. `mvn test`
4. `mvn verify`

`mvn test` and `mvn verify` succeed in the final UC-07 state.

## Coverage

- JaCoCo report directory:
  - `target/site/jacoco/index.html`
- Final branch coverage:
  - `BRANCH missed=0 covered=450 ratio=100.00%`

## Traceability

- `traceability/UC-07-traceability.md` maps AT-01..AT-04 to automated tests.

## Spec/Conflict Note

- `UC-07_tests.md` AT-04 expects a per-paper maximum referee limit failure.
- `specs/UC-07-assign-referees/clarifications.md` specifies no maximum referees per paper.
- Implementation follows the clarification and documents this conflict in UC-07 traceability.

## Spec-Kit Command Note

- `/speckit.implement` CLI command is not available in this environment (`/speckit.implement: no such file or directory`), so UC-07 was implemented directly from existing spec-kit artifacts.

---

# Lab 3 Report Notes (UC-08 Run)

## Scope Completed

- Implemented UC-08 reviewer workload enforcement flow from:
  - `specs/UC-08-enforce-reviewer-workload/spec.md`
  - `specs/UC-08-enforce-reviewer-workload/plan.md`
  - `specs/UC-08-enforce-reviewer-workload/tasks.md`
  - `specs/UC-08-enforce-reviewer-workload/contracts/workload-check.openapi.yaml`
  - `UC-08_tests.md`
- Added dedicated workload-check model/service layer:
  - `ReviewerWorkload` model
  - `WorkloadCheckService` interface
  - `DefaultWorkloadCheckService` implementation
- Updated referee assignment enforcement to use workload-check service with:
  - configurable workload limit policy (`cms.policy.reviewerWorkloadLimit`, default 5)
  - blocking behavior when workload retrieval fails
- Added UC-08 test coverage:
  - Unit tests for workload-check service and workload-limit policy parsing
  - Integration tests for under-limit, at-limit, and workload-retrieval-failure flows
  - Acceptance tests mapped to UC-08 AT-01..AT-03
- Added UC-08 traceability mapping.

## Commands Run

1. `/speckit.implement` (not available in this environment)
2. `mvn test`
3. `mvn verify`

`mvn test` and `mvn verify` succeed in the final UC-08 state.

## Coverage

- JaCoCo report directory:
  - `target/site/jacoco/index.html`
- Final branch coverage:
  - `BRANCH missed=0 covered=482 ratio=100.00%`

## Traceability

- `traceability/UC-08-traceability.md` maps AT-01..AT-03 to automated tests.

## Spec-Kit Command Note

- `/speckit.implement` CLI command is not available in this environment (`/speckit.implement: no such file or directory`), so UC-08 was implemented directly from existing spec-kit artifacts.

---

# Lab 3 Report Notes (UC-09 Run)

## Scope Completed

- Implemented UC-09 review invitation email flow from:
  - `specs/UC-09-receive-review-invitation-email/spec.md`
  - `specs/UC-09-receive-review-invitation-email/plan.md`
  - `specs/UC-09-receive-review-invitation-email/tasks.md`
  - `specs/UC-09-receive-review-invitation-email/contracts/review-invitation.openapi.yaml`
  - `UC-09_tests.md`
- Added invitation domain and supporting services:
  - `ReviewInvitation` model
  - `DeliveryFailureRecord` model
  - `ReviewInvitationService` with duplicate suppression, invalid-email handling, and failure logging
  - `InvitationComposer` with title/abstract/accept-reject instructions
  - in-memory repositories/services for invitation storage, delivery failures, email sending, and editor notifications
- Updated notification flow used by referee assignment to:
  - send invitation content
  - record delivery failures (invalid email or service outage)
  - notify editor on failures
  - suppress duplicate referee-paper invitations
  - avoid automatic retries
- Added UC-09 automated test coverage:
  - acceptance tests for AT-01..AT-03
  - integration tests for invitation flow
  - unit tests for invitation service, validation, and notification behavior
- Added UC-09 traceability mapping.

## Commands Run

1. `/speckit.implement` (not available in this environment)
2. `mvn test`
3. `mvn verify`

`mvn test` and `mvn verify` succeed in the final UC-09 state.

## Coverage

- JaCoCo report directory:
  - `target/site/jacoco/index.html`
- Final branch coverage:
  - `BRANCH missed=0 covered=510 ratio=100.00%`

## Traceability

- `traceability/UC-09-traceability.md` maps AT-01..AT-03 to automated tests.

## Spec-Kit Command Note

- `/speckit.implement` CLI command is not available in this environment (`/speckit.implement: no such file or directory`), so UC-09 was implemented directly from existing spec-kit artifacts.

---

# Lab 3 Report Notes (UC-10 Run)

## Scope Completed

- Implemented UC-10 accept/reject review invitation flow from:
  - `specs/UC-10-accept-reject-review-invitation/spec.md`
  - `specs/UC-10-accept-reject-review-invitation/plan.md`
  - `specs/UC-10-accept-reject-review-invitation/tasks.md`
  - `specs/UC-10-accept-reject-review-invitation/contracts/review-invitation-response.openapi.yaml`
  - `UC-10_tests.md`
- Added invitation response domain and service layer:
  - `InvitationResponse` model and result model
  - `InvitationResponseService` with accept/reject recording, single-response lock, expired invitation checks, and database-failure retry message handling
  - `InvitationResponseRepository` and in-memory implementation
  - `ReviewAssignmentService` for assignment status transitions (`pending` -> `accepted`/`rejected`)
- Extended invitation persistence and notification integration to support UC-10:
  - invitation lookup by id and status updates (`open`/`expired`/`responded`)
  - editor decision-only notifications (`accept` or `reject`)
  - invitation expiry support for response blocking and view blocking
- Added invitation response controller endpoint behavior:
  - `GET /invitations/{invitation_id}` blocks expired invitations
  - `POST /invitations/{invitation_id}/response` records decisions and returns assignment status
- Added UC-10 automated test coverage:
  - acceptance tests for AT-01..AT-04
  - integration endpoint tests for response flow
  - unit tests for service logic, servlet parsing/paths, assignment state transitions, and in-memory repositories
- Added UC-10 traceability mapping.

## Commands Run

1. `/speckit.implement` (not available in this environment)
2. `mvn test`
3. `mvn verify`

`mvn test` and `mvn verify` succeed in the final UC-10 state.

## Coverage

- JaCoCo report directory:
  - `target/site/jacoco/index.html`
- Final branch coverage:
  - `BRANCH missed=0 covered=568 ratio=100.00%`

## Traceability

- `traceability/UC-10-traceability.md` maps AT-01..AT-04 to automated tests.

## Spec-Kit Command Note

- `/speckit.implement` CLI command is not available in this environment (`/speckit.implement: no such file or directory`), so UC-10 was implemented directly from existing spec-kit artifacts.

---

# Lab 3 Report Notes (UC-13 Run)

## Scope Completed

- Implemented UC-13 final paper decision flow from:
  - `specs/UC-13-final-paper-decision/spec.md`
  - `specs/UC-13-final-paper-decision/plan.md`
  - `specs/UC-13-final-paper-decision/tasks.md`
  - `specs/UC-13-final-paper-decision/contracts/final-decision.openapi.yaml`
  - `UC-13_tests.md`
- Added final decision domain + service layer:
  - `FinalDecision` model
  - `FinalDecisionService` with decision validation, persistence, and retry-later handling on save errors
  - `FinalDecisionRepository` and `InMemoryFinalDecisionRepository`
  - `FinalDecisionNotificationService` integration during decision record
- Extended `/papers/*` endpoint handling to support:
  - `POST /papers/{paper_id}/decision`
  - validation that required reviews are completed (`submitted`) before decision recording
- Added UC-13 automated test coverage:
  - acceptance tests for AT-01..AT-04
  - integration endpoint tests for success/incomplete-reviews/storage-failure paths
  - unit tests for final decision service/repository and servlet routing
- Added UC-13 traceability mapping.

## Commands Run

1. `/speckit.implement` (not available in this environment)
2. `mvn test`
3. `mvn verify`

`mvn test` and `mvn verify` succeed in the final UC-13 state.

## Coverage

- JaCoCo report directory:
  - `target/site/jacoco/index.html`
- Final branch coverage:
  - `BRANCH missed=0 covered=870 ratio=100.00%`

## Traceability

- `traceability/UC-13-traceability.md` maps AT-01..AT-04 to automated tests.

## Spec-Kit Command Note

- `/speckit.implement` CLI command is not available in this environment (`/speckit.implement: no such file or directory`), so UC-13 was implemented directly from existing spec-kit artifacts.

---

# Lab 3 Report Notes (UC-14 Run)

## Scope Completed

- Implemented UC-14 final decision notification/status flow from:
  - `specs/UC-14-receive-final-decision/spec.md`
  - `specs/UC-14-receive-final-decision/plan.md`
  - `specs/UC-14-receive-final-decision/tasks.md`
  - `specs/UC-14-receive-final-decision/contracts/final-decision-notification.openapi.yaml`
  - `UC-14_tests.md`
- Added notification failure tracking + decision status access:
  - `NotificationDeliveryFailure` model
  - `NotificationFailureRepository` and `InMemoryNotificationFailureRepository`
  - final decision notification dispatch via email + failure logging + editor failure notification
- Extended `/papers/*` endpoint handling to support:
  - `POST /papers/{paper_id}/decision/notify`
  - `GET /papers/{paper_id}/decision`
- Ensured decision status remains viewable from CMS even if notification delivery fails.
- Added UC-14 automated test coverage:
  - acceptance tests for AT-01..AT-03
  - integration endpoint tests for notification success/failure and decision status access control
  - unit tests for notification service failure paths
- Added UC-14 traceability mapping.

## Commands Run

1. `/speckit.implement` (not available in this environment)
2. `mvn test`
3. `mvn verify`

`mvn test` and `mvn verify` succeed in the final UC-14 state.

## Coverage

- JaCoCo report directory:
  - `target/site/jacoco/index.html`
- Final branch coverage:
  - `BRANCH missed=0 covered=870 ratio=100.00%`

## Traceability

- `traceability/UC-14-traceability.md` maps AT-01..AT-03 to automated tests.

## Spec-Kit Command Note

- `/speckit.implement` CLI command is not available in this environment (`/speckit.implement: no such file or directory`), so UC-14 was implemented directly from existing spec-kit artifacts.
