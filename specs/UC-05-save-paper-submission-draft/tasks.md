# Tasks: Save Paper Submission Draft

**Branch**: `UC-05-save-paper-submission-draft`  
**Plan**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-05-save-paper-submission-draft/plan.md  
**Spec**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-05-save-paper-submission-draft/spec.md  
**Date**: 2026-02-05

## Dependencies

- User Story 1 (P1) must be completed before User Story 2 (P2) and User Story 3
  (P3) because it establishes the core draft save flow.
- User Story 2 (P2) and User Story 3 (P3) can be implemented in parallel after
  User Story 1 (P1) is complete.

## Implementation Strategy

- MVP scope: User Story 1 (core draft save with confirmation).
- Incremental delivery: Add validation errors (P2), then storage error handling
  and no-change save behavior (P3).

## Phase 1: Setup

- [ ] T001 Create MVC directories per plan in src/controllers, src/models,
  src/services, src/views, src/static
- [ ] T002 Create test directories per plan in tests/contract, tests/integration,
  tests/unit

## Phase 2: Foundational

- [ ] T003 [P] Define Paper Submission Draft model interface in
  src/models/PaperSubmissionDraft.java
- [ ] T004 [P] Define draft validation service interface in
  src/services/DraftValidationService.java
- [ ] T005 [P] Define draft save service interface in
  src/services/DraftSaveService.java

## Phase 3: User Story 1 (P1) - Save Draft Successfully

**Story Goal**: Save the current submission state as a draft and confirm success.

**Independent Test**: Enter title + corresponding author email, save, and verify
confirmation.

- [ ] T006 [US1] Create draft save controller in
  src/controllers/DraftSaveController.java
- [ ] T007 [US1] Implement draft save action in submission form view in
  src/views/submit-paper.html
- [ ] T008 [US1] Wire draft save route and handler in
  src/controllers/DraftSaveController.java
- [ ] T009 [US1] Implement draft save orchestration in
  src/services/DraftSaveService.java
- [ ] T010 [US1] Implement draft persistence stub in
  src/models/PaperSubmissionDraft.java
- [ ] T011 [US1] Render draft saved confirmation message in
  src/views/submit-paper.html

## Phase 4: User Story 2 (P2) - Handle Invalid Draft Data

**Story Goal**: Block saves with invalid/incomplete draft data and show errors.

**Independent Test**: Attempt save without required fields and verify error.

- [ ] T012 [US2] Add draft validation rules (title + corresponding author email)
  in src/services/DraftValidationService.java
- [ ] T013 [US2] Surface draft validation errors in
  src/services/DraftSaveService.java
- [ ] T014 [US2] Render validation error messaging in
  src/views/submit-paper.html

## Phase 5: User Story 3 (P3) - Handle Storage Errors and No-Change Saves

**Story Goal**: Show errors on storage failures and confirm no-change saves.

**Independent Test**: Simulate storage error and verify error message; click save
with no changes and verify confirmation.

- [ ] T015 [US3] Add storage error handling in
  src/services/DraftSaveService.java
- [ ] T016 [US3] Add no-change save detection and confirmation in
  src/services/DraftSaveService.java
- [ ] T017 [US3] Render storage error messaging in
  src/views/submit-paper.html
- [ ] T018 [US3] Render no-change save confirmation in
  src/views/submit-paper.html

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T019 Ensure draft save success and error messages align with spec wording in
  src/views/submit-paper.html
- [ ] T020 Ensure minimum required fields (title + corresponding author email)
  are documented in UI helper text in src/views/submit-paper.html

## Parallel Execution Opportunities

- US2 tasks T012-T014 can be executed in parallel after US1.
- US3 tasks T015-T018 can be executed in parallel after US1.
- Foundational interface tasks T003-T005 can be done in parallel.

## Format Validation

- All tasks use checkbox format with IDs and file paths.
