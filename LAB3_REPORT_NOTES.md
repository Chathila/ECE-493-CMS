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
