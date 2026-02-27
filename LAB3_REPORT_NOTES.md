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
