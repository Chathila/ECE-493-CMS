# Tasks: Register User Account

**Branch**: `001-user-registration`  
**Plan**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/001-user-registration/plan.md  
**Spec**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/001-user-registration/spec.md  
**Date**: 2026-02-05

## Dependencies

- User Story 1 (P1) must be completed before User Story 2 (P2) and User Story 3
  (P3) because it establishes the core registration flow and persistence.
- User Story 2 (P2) and User Story 3 (P3) can be implemented in parallel after
  User Story 1 (P1) is complete.

## Implementation Strategy

- MVP scope: User Story 1 (core registration flow with email + password, account
  creation, confirmation, and redirect).
- Incremental delivery: Add validation error handling (P2), then duplicate/weak
  credential handling and outage responses (P3).

## Phase 1: Setup

- [ ] T001 Create MVC directories per plan in src/controllers, src/models,
  src/services, src/views, src/static
- [ ] T002 Create test directories per plan in tests/contract, tests/integration,
  tests/unit

## Phase 2: Foundational

- [ ] T003 [P] Define User Account model interface in src/models/UserAccount.java
- [ ] T004 [P] Define Registration Submission model interface in
  src/models/RegistrationSubmission.java
- [ ] T005 [P] Define registration service interface for validation and creation
  in src/services/RegistrationService.java
- [ ] T006 [P] Define email validation service interface in
  src/services/EmailValidationService.java
- [ ] T007 [P] Define password policy validation interface in
  src/services/PasswordPolicyService.java

## Phase 3: User Story 1 (P1) - Register a New Account

**Story Goal**: Allow a new user to register with email + password, create an
account, show confirmation, and redirect to login.

**Independent Test**: Submit valid email + password and verify account creation,
confirmation message, and redirect.

- [ ] T008 [US1] Create registration controller in
  src/controllers/RegistrationController.java
- [ ] T009 [US1] Implement registration form view in
  src/views/register.html
- [ ] T010 [US1] Wire registration route and handlers in
  src/controllers/RegistrationController.java
- [ ] T011 [US1] Implement registration flow orchestration in
  src/services/RegistrationService.java
- [ ] T012 [US1] Implement account persistence stub in
  src/models/UserAccount.java
- [ ] T013 [US1] Implement confirmation message and redirect behavior in
  src/views/register.html

## Phase 4: User Story 2 (P2) - Fix Missing or Invalid Input

**Story Goal**: Provide clear feedback for missing fields and invalid email.

**Independent Test**: Submit empty fields or invalid email and verify errors with
no account creation.

- [ ] T014 [US2] Add required-field validation in
  src/services/RegistrationService.java
- [ ] T015 [US2] Add email format validation in
  src/services/RegistrationService.java
- [ ] T016 [US2] Render missing-field error messaging in src/views/register.html
- [ ] T017 [US2] Render invalid-email error messaging in src/views/register.html

## Phase 5: User Story 3 (P3) - Resolve Duplicate or Weak Credentials

**Story Goal**: Handle duplicate email, weak password, and email validation
service outage errors.

**Independent Test**: Submit duplicate email, weak password, or simulate service
outage and verify correct error messaging and no account creation.

- [ ] T018 [US3] Add email uniqueness check in
  src/services/RegistrationService.java
- [ ] T019 [US3] Add password policy enforcement hook in
  src/services/RegistrationService.java
- [ ] T020 [US3] Add email validation service outage handling in
  src/services/RegistrationService.java
- [ ] T021 [US3] Render duplicate-email error messaging in
  src/views/register.html
- [ ] T022 [US3] Render password policy error messaging in
  src/views/register.html
- [ ] T023 [US3] Render service-outage error messaging in
  src/views/register.html

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T024 Ensure registration success and error messages align with spec wording
  in src/views/register.html
- [ ] T025 Ensure email-as-username is documented in UI helper text in
  src/views/register.html
- [ ] T026 Ensure no plaintext password handling in models/services
  (src/models/UserAccount.java, src/services/RegistrationService.java)

## Parallel Execution Opportunities

- US2 tasks T014-T017 can be executed in parallel after US1.
- US3 tasks T018-T023 can be executed in parallel after US1.
- Foundational interface tasks T003-T007 can be done in parallel.

## Format Validation

- All tasks use checkbox format with IDs and file paths.
