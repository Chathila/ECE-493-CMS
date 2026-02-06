# Tasks: Enforce Reviewer Workload Limit

**Branch**: `UC-08-enforce-reviewer-workload`  
**Plan**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-08-enforce-reviewer-workload/plan.md  
**Spec**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-08-enforce-reviewer-workload/spec.md  
**Date**: 2026-02-05

## Dependencies

- User Story 1 (P1) must be completed before User Story 2 (P2) and User Story 3
  (P3) because it establishes the core workload check flow.
- User Story 2 (P2) and User Story 3 (P3) can be implemented in parallel after
  User Story 1 (P1) is complete.

## Implementation Strategy

- MVP scope: User Story 1 (workload check and allow assignment under limit).
- Incremental delivery: Add limit-blocking behavior (P2), then retrieval failure
  handling (P3).

## Phase 1: Setup

- [ ] T001 Create MVC directories per plan in src/controllers, src/models,
  src/services, src/views, src/static
- [ ] T002 Create test directories per plan in tests/contract, tests/integration,
  tests/unit

## Phase 2: Foundational

- [ ] T003 [P] Define Reviewer Workload model interface in
  src/models/ReviewerWorkload.java
- [ ] T004 [P] Define Referee Assignment model interface in
  src/models/RefereeAssignment.java
- [ ] T005 [P] Define workload check service interface in
  src/services/WorkloadCheckService.java

## Phase 3: User Story 1 (P1) - Allow Assignment Under Limit

**Story Goal**: Allow assignments when reviewer workload is below the limit.

**Independent Test**: Assign a reviewer under the limit and verify success and
count update.

- [ ] T006 [US1] Create workload check controller in
  src/controllers/WorkloadCheckController.java
- [ ] T007 [US1] Implement workload check handler in
  src/controllers/WorkloadCheckController.java
- [ ] T008 [US1] Implement workload check logic in
  src/services/WorkloadCheckService.java
- [ ] T009 [US1] Implement workload count update in
  src/models/ReviewerWorkload.java

## Phase 4: User Story 2 (P2) - Block Assignment at Limit

**Story Goal**: Block assignments when reviewer is at the workload limit.

**Independent Test**: Attempt assignment at limit and verify violation message.

- [ ] T010 [US2] Add workload limit comparison in
  src/services/WorkloadCheckService.java
- [ ] T011 [US2] Render workload violation error messaging in
  src/views/assign-referees.html

## Phase 5: User Story 3 (P3) - Handle Workload Retrieval Failure

**Story Goal**: Block assignments when workload data cannot be retrieved.

**Independent Test**: Simulate retrieval failure and verify error.

- [ ] T012 [US3] Add workload retrieval failure handling in
  src/services/WorkloadCheckService.java
- [ ] T013 [US3] Render workload retrieval error messaging in
  src/views/assign-referees.html

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T014 Ensure workload success and error messages align with spec wording in
  src/views/assign-referees.html
- [ ] T015 Ensure configured workload limit (default 5) is communicated in UI
  helper text in src/views/assign-referees.html

## Parallel Execution Opportunities

- US2 tasks T010-T011 can be executed in parallel after US1.
- US3 tasks T012-T013 can be executed in parallel after US1.
- Foundational interface tasks T003-T005 can be done in parallel.

## Format Validation

- All tasks use checkbox format with IDs and file paths.
