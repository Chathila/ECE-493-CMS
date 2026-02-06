# Tasks: Make Final Paper Decision

**Input**: Design documents from `/specs/UC-13-final-paper-decision/`
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
- [ ] T002 [P] Add UC-13 contract file reference to specs/UC-13-final-paper-decision/contracts/README.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 [P] Define FinalDecision model (decision, decided_at, paper_id, editor_id) in src/models/FinalDecision.java
- [ ] T004 [P] Define Review model (paper_id, submitted_at, referee_id) in src/models/Review.java
- [ ] T005 [P] Add or extend Paper model (title, abstract) in src/models/Paper.java
- [ ] T006 [P] Add or extend Editor and Author models (email) in src/models/Editor.java and src/models/Author.java
- [ ] T007 [P] Create repositories for decisions and reviews in src/services/FinalDecisionRepository.java and src/services/ReviewRepository.java
- [ ] T008 Add decision validation service for review completeness in src/services/DecisionValidationService.java
- [ ] T009 Create AuthorNotificationService interface and stub implementation in src/services/AuthorNotificationService.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Editor records final decision (Priority: P1) 🎯 MVP

**Goal**: Display reviews, accept a final decision, record it, and notify the author.

**Independent Test**: Submit Accept/Reject on a paper with completed reviews and verify decision is stored and author notified.

### Implementation for User Story 1

- [ ] T010 [P] [US1] Implement decision submission service in src/services/FinalDecisionService.java (depends on T003, T007, T008)
- [ ] T011 [US1] Record decision in src/services/FinalDecisionService.java (depends on T010)
- [ ] T012 [US1] Notify author of decision in src/services/AuthorNotificationService.java (depends on T009, T010)
- [ ] T013 [US1] Add decision submission endpoint per contract in src/controllers/FinalDecisionController.java (depends on T010)
- [ ] T014 [US1] Display submitted reviews and evaluation details in src/views/final-decision.html (depends on T004, T005)

**Checkpoint**: User Story 1 should now be fully functional and testable independently

---

## Phase 4: User Story 2 - Reviews incomplete error (Priority: P2)

**Goal**: Block submission and show reviews-incomplete error on decision page when reviews are missing.

**Independent Test**: Attempt decision with incomplete reviews and verify decision-page error with no record.

### Implementation for User Story 2

- [ ] T015 [US2] Enforce review-complete check in src/services/DecisionValidationService.java (depends on T008)
- [ ] T016 [US2] Return reviews-incomplete error on decision page in src/controllers/FinalDecisionController.java (depends on T013, T015)
- [ ] T017 [US2] Add reviews-incomplete messaging in src/views/final-decision.html (depends on T014)

**Checkpoint**: User Stories 1 and 2 should both work independently

---

## Phase 5: User Story 3 - Decision recording failure (Priority: P3)

**Goal**: Handle database errors with retry-later message and no decision recorded.

**Independent Test**: Simulate database error and verify retry-later message with no decision stored.

### Implementation for User Story 3

- [ ] T018 [US3] Handle database errors and return retry-later message in src/services/FinalDecisionService.java (depends on T010)
- [ ] T019 [US3] Return retry-later response in src/controllers/FinalDecisionController.java (depends on T013, T018)

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T020 [P] Update documentation references in specs/UC-13-final-paper-decision/quickstart.md to reflect implementation notes
- [ ] T021 [P] Run quickstart.md validation steps and record findings in specs/UC-13-final-paper-decision/quickstart.md

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
- T015 and T016 can run in parallel after T013

---

## Parallel Example: User Story 1

```bash
Task: "Implement decision submission service in src/services/FinalDecisionService.java"
Task: "Notify author of decision in src/services/AuthorNotificationService.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. STOP and validate User Story 1 independently
5. Proceed to User Story 2 and 3 after MVP validation
