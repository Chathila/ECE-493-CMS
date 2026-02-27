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
