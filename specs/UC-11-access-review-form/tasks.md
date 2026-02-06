# Tasks: Access Paper Review Form

**Input**: Design documents from `/specs/UC-11-access-review-form/`
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
- [ ] T002 [P] Add UC-11 contract file reference to specs/UC-11-access-review-form/contracts/README.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 [P] Define ReviewForm model (form_id, version) in src/models/ReviewForm.java
- [ ] T004 [P] Add or extend Paper model (title, abstract) in src/models/Paper.java
- [ ] T005 [P] Add or extend Referee model (email) in src/models/Referee.java
- [ ] T006 [P] Add or extend ReviewAssignment model (status, paper_id, referee_id) in src/models/ReviewAssignment.java
- [ ] T007 [P] Create repository interfaces for review form and assignments in src/services/ReviewFormRepository.java and src/services/ReviewAssignmentRepository.java
- [ ] T008 Add authorization helper for assignment accepted status in src/services/ReviewAuthorizationService.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Referee accesses review form (Priority: P1) 🎯 MVP

**Goal**: Display the review form and paper details for an authorized referee.

**Independent Test**: Select an assigned paper and verify the review form and paper details display.

### Implementation for User Story 1

- [ ] T009 [P] [US1] Implement review form retrieval service in src/services/ReviewFormService.java (depends on T003, T004, T007, T008)
- [ ] T010 [US1] Implement controller endpoint per contract in src/controllers/ReviewFormController.java (depends on T009)
- [ ] T011 [US1] Build view rendering logic for review form display in src/views/review-form.html (depends on T010)

**Checkpoint**: User Story 1 should now be fully functional and testable independently

---

## Phase 4: User Story 2 - Access denied for unauthorized referee (Priority: P2)

**Goal**: Deny access and show access-denied message when unauthorized.

**Independent Test**: Attempt access when unassigned/not accepted and verify access-denied message with no form.

### Implementation for User Story 2

- [ ] T012 [US2] Enforce authorization failure handling in src/services/ReviewAuthorizationService.java (depends on T008)
- [ ] T013 [US2] Return access-denied response and stop form load in src/controllers/ReviewFormController.java (depends on T010, T012)
- [ ] T014 [US2] Add access-denied messaging in src/views/review-form.html (depends on T011)

**Checkpoint**: User Stories 1 and 2 should both work independently

---

## Phase 5: User Story 3 - Review form unavailable (Priority: P3)

**Goal**: Show form-unavailable message when retrieval fails.

**Independent Test**: Simulate retrieval failure and verify "form unavailable" message with no form display.

### Implementation for User Story 3

- [ ] T015 [US3] Surface form-unavailable errors from retrieval failures in src/services/ReviewFormService.java (depends on T009)
- [ ] T016 [US3] Return form-unavailable response in src/controllers/ReviewFormController.java (depends on T010, T015)
- [ ] T017 [US3] Add form-unavailable messaging in src/views/review-form.html (depends on T011)

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T018 [P] Update documentation references in specs/UC-11-access-review-form/quickstart.md to reflect implementation notes
- [ ] T019 [P] Run quickstart.md validation steps and record findings in specs/UC-11-access-review-form/quickstart.md

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
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Depends on US1 controller flow
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Depends on US1 controller flow

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- T003, T004, T005, T006, T007 can run in parallel
- T009 and T011 can run in parallel after foundations
- T012 and T013 can run in parallel after T010

---

## Parallel Example: User Story 1

```bash
Task: "Implement review form retrieval service in src/services/ReviewFormService.java"
Task: "Build view rendering logic in src/views/review-form.html"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. STOP and validate User Story 1 independently
5. Proceed to User Story 2 and 3 after MVP validation
