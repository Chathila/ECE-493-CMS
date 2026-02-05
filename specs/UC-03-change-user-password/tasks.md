# Tasks: Change User Password

**Branch**: `UC-03-change-user-password`  
**Plan**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-03-change-user-password/plan.md  
**Spec**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-03-change-user-password/spec.md  
**Date**: 2026-02-05

## Dependencies

- User Story 1 (P1) must be completed before User Story 2 (P2) and User Story 3
  (P3) because it establishes the core password change flow and validation.
- User Story 2 (P2) and User Story 3 (P3) can be implemented in parallel after
  User Story 1 (P1) is complete.

## Implementation Strategy

- MVP scope: User Story 1 (core password change flow with confirmation and
  re-login requirement).
- Incremental delivery: Add invalid current password handling (P2), then weak
  password/mismatch handling and outage responses (P3).

## Phase 1: Setup

- [ ] T001 Create MVC directories per plan in src/controllers, src/models,
  src/services, src/views, src/static
- [ ] T002 Create test directories per plan in tests/contract, tests/integration,
  tests/unit

## Phase 2: Foundational

- [ ] T003 [P] Define User Account model interface in src/models/UserAccount.java
- [ ] T004 [P] Define Password Change Request model interface in
  src/models/PasswordChangeRequest.java
- [ ] T005 [P] Define password change service interface in
  src/services/PasswordChangeService.java
- [ ] T006 [P] Define password policy validation interface in
  src/services/PasswordPolicyService.java
- [ ] T007 [P] Define authentication service interface for current password
  verification in src/services/AuthenticationService.java

## Phase 3: User Story 1 (P1) - Change Password Successfully

**Story Goal**: Allow a logged-in user to change their password and require
re-login after success.

**Independent Test**: Submit valid current password and compliant new password
and verify confirmation plus re-login requirement.

- [ ] T008 [US1] Create change password controller in
  src/controllers/ChangePasswordController.java
- [ ] T009 [US1] Implement change password form view in
  src/views/change-password.html
- [ ] T010 [US1] Wire change password route and handlers in
  src/controllers/ChangePasswordController.java
- [ ] T011 [US1] Implement password change orchestration in
  src/services/PasswordChangeService.java
- [ ] T012 [US1] Implement password update persistence stub in
  src/models/UserAccount.java
- [ ] T013 [US1] Implement confirmation message and re-login requirement in
  src/views/change-password.html

## Phase 4: User Story 2 (P2) - Handle Invalid Current Password

**Story Goal**: Block password changes when the current password is invalid and
show an error.

**Independent Test**: Submit incorrect current password and verify no change with
an error message.

- [ ] T014 [US2] Add current password verification in
  src/services/AuthenticationService.java
- [ ] T015 [US2] Surface invalid current password error in
  src/services/PasswordChangeService.java
- [ ] T016 [US2] Render invalid-current-password error messaging in
  src/views/change-password.html

## Phase 5: User Story 3 (P3) - Handle Invalid New Password Inputs

**Story Goal**: Enforce password policy, detect mismatched confirmation, and
handle authentication service outages.

**Independent Test**: Submit weak new password, mismatched confirmation, or
simulate service outage and verify correct errors and no update.

- [ ] T017 [US3] Add password policy validation in
  src/services/PasswordPolicyService.java
- [ ] T018 [US3] Add new password and confirmation match check in
  src/services/PasswordChangeService.java
- [ ] T019 [US3] Add authentication service outage handling in
  src/services/PasswordChangeService.java
- [ ] T020 [US3] Render weak-password error messaging in
  src/views/change-password.html
- [ ] T021 [US3] Render mismatch error messaging in
  src/views/change-password.html
- [ ] T022 [US3] Render service-outage error messaging in
  src/views/change-password.html

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T023 Ensure success and error messages align with spec wording in
  src/views/change-password.html
- [ ] T024 Ensure re-login requirement is communicated in UI text in
  src/views/change-password.html
- [ ] T025 Ensure no plaintext password handling in models/services
  (src/models/UserAccount.java, src/services/PasswordChangeService.java)

## Parallel Execution Opportunities

- US2 tasks T014-T016 can be executed in parallel after US1.
- US3 tasks T017-T022 can be executed in parallel after US1.
- Foundational interface tasks T003-T007 can be done in parallel.

## Format Validation

- All tasks use checkbox format with IDs and file paths.
