# Tasks: User Login

**Branch**: `UC-02-user-login`  
**Plan**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-02-user-login/plan.md  
**Spec**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-02-user-login/spec.md  
**Date**: 2026-02-05

## Dependencies

- User Story 1 (P1) must be completed before User Story 2 (P2) and User Story 3
  (P3) because it establishes the core login flow and authentication check.
- User Story 2 (P2) and User Story 3 (P3) can be implemented in parallel after
  User Story 1 (P1) is complete.

## Implementation Strategy

- MVP scope: User Story 1 (core login flow with authentication and redirect).
- Incremental delivery: Add validation error handling (P2), then inactive/locked
  account handling and outage responses (P3).

## Phase 1: Setup

- [ ] T001 Create MVC directories per plan in src/controllers, src/models,
  src/services, src/views, src/static
- [ ] T002 Create test directories per plan in tests/contract, tests/integration,
  tests/unit

## Phase 2: Foundational

- [ ] T003 [P] Define User Account model interface in src/models/UserAccount.java
- [ ] T004 [P] Define Authentication Attempt model interface in
  src/models/AuthenticationAttempt.java
- [ ] T005 [P] Define authentication service interface for credential validation
  in src/services/AuthenticationService.java
- [ ] T006 [P] Define account status check interface in
  src/services/AccountStatusService.java

## Phase 3: User Story 1 (P1) - Authenticate to Access CMS

**Story Goal**: Allow a registered user to log in with email + password and be
redirected to the authorized home page.

**Independent Test**: Submit valid credentials and verify authentication success
and redirect to authorized home page.

- [ ] T007 [US1] Create login controller in
  src/controllers/LoginController.java
- [ ] T008 [US1] Implement login form view in src/views/login.html
- [ ] T009 [US1] Wire login route and handlers in
  src/controllers/LoginController.java
- [ ] T010 [US1] Implement authentication flow orchestration in
  src/services/AuthenticationService.java
- [ ] T011 [US1] Implement account lookup and credential check stub in
  src/models/UserAccount.java
- [ ] T012 [US1] Implement redirect to authorized home page in
  src/views/login.html

## Phase 4: User Story 2 (P2) - Handle Missing or Invalid Credentials

**Story Goal**: Provide clear feedback for missing fields and incorrect
credentials.

**Independent Test**: Submit empty fields or incorrect credentials and verify
errors with no authentication.

- [ ] T013 [US2] Add required-field validation in
  src/services/AuthenticationService.java
- [ ] T014 [US2] Add authentication failure handling for incorrect credentials in
  src/services/AuthenticationService.java
- [ ] T015 [US2] Render missing-field error messaging in src/views/login.html
- [ ] T016 [US2] Render authentication failure messaging in src/views/login.html

## Phase 5: User Story 3 (P3) - Handle Inactive or Locked Accounts

**Story Goal**: Deny access for inactive or locked accounts and show a status
message; block login when auth service is unavailable.

**Independent Test**: Submit inactive/locked account credentials or simulate
service outage and verify access is denied with correct messaging.

- [ ] T017 [US3] Add inactive/locked account check in
  src/services/AccountStatusService.java
- [ ] T018 [US3] Add authentication service outage handling in
  src/services/AuthenticationService.java
- [ ] T019 [US3] Render account status messaging in src/views/login.html
- [ ] T020 [US3] Render service-outage error messaging in src/views/login.html

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T021 Ensure login success and error messages align with spec wording in
  src/views/login.html
- [ ] T022 Ensure email-as-username is documented in UI helper text in
  src/views/login.html
- [ ] T023 Ensure no plaintext password handling in models/services
  (src/models/UserAccount.java, src/services/AuthenticationService.java)

## Parallel Execution Opportunities

- US2 tasks T013-T016 can be executed in parallel after US1.
- US3 tasks T017-T020 can be executed in parallel after US1.
- Foundational interface tasks T003-T006 can be done in parallel.

## Format Validation

- All tasks use checkbox format with IDs and file paths.
