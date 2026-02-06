# Tasks: Assign Referees to Submitted Papers

**Branch**: `UC-07-assign-referees`  
**Plan**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-07-assign-referees/plan.md  
**Spec**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-07-assign-referees/spec.md  
**Date**: 2026-02-05

## Dependencies

- User Story 1 (P1) must be completed before User Story 2 (P2) and User Story 3
  (P3) because it establishes the core assignment flow.
- User Story 2 (P2) and User Story 3 (P3) can be implemented in parallel after
  User Story 1 (P1) is complete.

## Implementation Strategy

- MVP scope: User Story 1 (assign referees and send invitations).
- Incremental delivery: Add invalid email handling (P2), then workload/limit and
  notification failure handling (P3).

## Phase 1: Setup

- [ ] T001 Create MVC directories per plan in src/controllers, src/models,
  src/services, src/views, src/static
- [ ] T002 Create test directories per plan in tests/contract, tests/integration,
  tests/unit

## Phase 2: Foundational

- [ ] T003 [P] Define Referee model interface in src/models/Referee.java
- [ ] T004 [P] Define Referee Assignment model interface in
  src/models/RefereeAssignment.java
- [ ] T005 [P] Define assignment service interface in
  src/services/RefereeAssignmentService.java
- [ ] T006 [P] Define notification service interface in
  src/services/NotificationService.java

## Phase 3: User Story 1 (P1) - Assign Referees Successfully

**Story Goal**: Assign referees to a paper and send invitations.

**Independent Test**: Select valid referee emails and verify assignments stored
and invitations sent.

- [ ] T007 [US1] Create referee assignment controller in
  src/controllers/RefereeAssignmentController.java
- [ ] T008 [US1] Implement assignment interface view in
  src/views/assign-referees.html
- [ ] T009 [US1] Wire assignment route and handlers in
  src/controllers/RefereeAssignmentController.java
- [ ] T010 [US1] Implement assignment orchestration in
  src/services/RefereeAssignmentService.java
- [ ] T011 [US1] Implement assignment persistence stub in
  src/models/RefereeAssignment.java
- [ ] T012 [US1] Implement invitation sending hook in
  src/services/NotificationService.java
- [ ] T013 [US1] Render assignment confirmation message in
  src/views/assign-referees.html

## Phase 4: User Story 2 (P2) - Handle Invalid Referee Selection

**Story Goal**: Block invalid referee emails and show error.

**Independent Test**: Enter invalid referee email and verify error with no
assignment.

- [ ] T014 [US2] Add referee email validation in
  src/services/RefereeAssignmentService.java
- [ ] T015 [US2] Render invalid referee error messaging in
  src/views/assign-referees.html

## Phase 5: User Story 3 (P3) - Enforce Assignment Limits

**Story Goal**: Enforce workload limits and handle notification failures.

**Independent Test**: Select overworked referee or simulate notification failure
and verify workload/notification messages.

- [ ] T016 [US3] Add referee workload limit checks in
  src/services/RefereeAssignmentService.java
- [ ] T017 [US3] Handle notification failures and warnings in
  src/services/RefereeAssignmentService.java
- [ ] T018 [US3] Render workload violation messaging in
  src/views/assign-referees.html
- [ ] T019 [US3] Render notification warning messaging in
  src/views/assign-referees.html

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T020 Ensure assignment success and error messages align with spec wording
  in src/views/assign-referees.html
- [ ] T021 Ensure minimum referee requirement is communicated in UI helper text
  in src/views/assign-referees.html

## Parallel Execution Opportunities

- US2 tasks T014-T015 can be executed in parallel after US1.
- US3 tasks T016-T019 can be executed in parallel after US1.
- Foundational interface tasks T003-T006 can be done in parallel.

## Format Validation

- All tasks use checkbox format with IDs and file paths.
