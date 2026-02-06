# Tasks: Submit Paper Review

**Input**: Design documents from `/specs/UC-12-submit-paper-review/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: No test tasks included (not explicitly requested in the feature specification).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Confirm MVC folder structure exists or create missing directories in src/controllers/, src/models/, src/services/, src/views/, src/static/
- [ ] T002 [P] Add UC-12 contract file reference to specs/UC-12-submit-paper-review/contracts/README.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 [P] Define Review model (assignment_id, submitted_at, status) in src/models/Review.java
- [ ] T004 [P] Define ReviewForm model (responses) in src/models/ReviewForm.java
- [ ] T005 [P] Add or extend ReviewAssignment model (status) in src/models/ReviewAssignment.java
- [ ] T006 [P] Add or extend Paper and Referee models in src/models/Paper.java and src/models/Referee.java
- [ ] T007 [P] Create repositories for reviews and assignments in src/services/ReviewRepository.java and src/services/ReviewAssignmentRepository.java
- [ ] T008 Add validation service wrapper for field-level validation in src/services/ReviewValidationService.java
- [ ] T009 Create EditorNotificationService interface and stub implementation in src/services/EditorNotificationService.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Referee submits completed review (Priority: P1) 🎯 MVP

**Goal**: Validate, store, and notify on successful review submission.

**Independent Test**: Submit a completed review and confirm it is stored and the editor is notified.

### Implementation for User Story 1

- [ ] T010 [P] [US1] Implement review submission service in src/services/ReviewSubmissionService.java (depends on T003, T004, T007, T008)
- [ ] T011 [US1] Store review and update assignment status in src/services/ReviewSubmissionService.java (depends on T010, T005)
- [ ] T012 [US1] Notify editor of submission in src/services/EditorNotificationService.java (depends on T009, T010)
- [ ] T013 [US1] Add submission endpoint per contract in src/controllers/ReviewSubmissionController.java (depends on T010)

**Checkpoint**: User Story 1 should now be fully functional and testable independently

---

## Phase 4: User Story 2 - Validation errors on incomplete or invalid fields (Priority: P2)

**Goal**: Block submission and show field-level validation errors when invalid.

**Independent Test**: Submit with missing/invalid fields and verify field-level errors with no storage.

### Implementation for User Story 2

- [ ] T014 [US2] Return field-level validation errors in src/services/ReviewSubmissionService.java (depends on T008, T010)
- [ ] T015 [US2] Map validation errors to response in src/controllers/ReviewSubmissionController.java (depends on T013, T014)

**Checkpoint**: User Stories 1 and 2 should both work independently

---

## Phase 5: User Story 3 - Submission failure on storage error (Priority: P3)

**Goal**: Handle storage errors with retry-later message and no stored review.

**Independent Test**: Simulate storage failure and verify retry-later message with no stored review.

### Implementation for User Story 3

- [ ] T016 [US3] Handle storage failures and return retry-later message in src/services/ReviewSubmissionService.java (depends on T010)
- [ ] T017 [US3] Return retry-later response in src/controllers/ReviewSubmissionController.java (depends on T013, T016)

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T018 [P] Update documentation references in specs/UC-12-submit-paper-review/quickstart.md to reflect implementation notes
- [ ] T019 [P] Run quickstart.md validation steps and record findings in specs/UC-12-submit-paper-review/quickstart.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Depends on US1 service flow
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Depends on US1 service flow

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- T003, T004, T005, T006, T007 can run in parallel
- T010 and T012 can run in parallel after foundations
- T014 and T015 can run in parallel after T013

---

## Parallel Example: User Story 1

```bash
Task: "Implement review submission service in src/services/ReviewSubmissionService.java"
Task: "Notify editor of submission in src/services/EditorNotificationService.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. STOP and validate User Story 1 independently
5. Proceed to User Story 2 and 3 after MVP validation
