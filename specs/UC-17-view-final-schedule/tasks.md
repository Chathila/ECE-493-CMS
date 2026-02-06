# Tasks: View Final Conference Schedule

**Input**: Design documents from `/specs/UC-17-view-final-schedule/`
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
- [ ] T002 [P] Add UC-17 contract file reference to specs/UC-17-view-final-schedule/contracts/README.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 [P] Define Schedule model (published_at, status) in src/models/Schedule.java
- [ ] T004 [P] Define Session model (time_slot, location, title, schedule_id) in src/models/Session.java
- [ ] T005 [P] Create repository for schedule retrieval in src/services/ScheduleRepository.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - User views final schedule (Priority: P1) 🎯 MVP

**Goal**: Retrieve and display the published schedule in a readable format.

**Independent Test**: Request final schedule and verify session times, locations, and titles display.

### Implementation for User Story 1

- [ ] T006 [P] [US1] Implement schedule retrieval service in src/services/ScheduleViewService.java (depends on T003, T004, T005)
- [ ] T007 [US1] Add schedule view endpoint per contract in src/controllers/ScheduleViewController.java (depends on T006)
- [ ] T008 [US1] Render schedule display in src/views/final-schedule.html (depends on T007)

**Checkpoint**: User Story 1 should now be fully functional and testable independently

---

## Phase 4: User Story 2 - Schedule not published (Priority: P2)

**Goal**: Show not-published message when schedule is unavailable.

**Independent Test**: Request schedule before publication and verify message.

### Implementation for User Story 2

- [ ] T009 [US2] Detect unpublished schedule state in src/services/ScheduleViewService.java (depends on T003, T005)
- [ ] T010 [US2] Return not-published message in src/controllers/ScheduleViewController.java (depends on T007, T009)
- [ ] T011 [US2] Render not-published message in src/views/final-schedule.html (depends on T008, T010)

**Checkpoint**: User Stories 1 and 2 should both work independently

---

## Phase 5: User Story 3 - Schedule retrieval failure (Priority: P3)

**Goal**: Show retry-later message when retrieval fails.

**Independent Test**: Simulate database error and verify retry-later message and no schedule.

### Implementation for User Story 3

- [ ] T012 [US3] Handle retrieval failures in src/services/ScheduleViewService.java (depends on T006)
- [ ] T013 [US3] Return retry-later response in src/controllers/ScheduleViewController.java (depends on T007, T012)
- [ ] T014 [US3] Render retry-later message in src/views/final-schedule.html (depends on T008, T013)

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T015 [P] Update documentation references in specs/UC-17-view-final-schedule/quickstart.md to reflect implementation notes
- [ ] T016 [P] Run quickstart.md validation steps and record findings in specs/UC-17-view-final-schedule/quickstart.md

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
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Depends on US1 retrieval flow
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Depends on US1 retrieval flow

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- T003, T004, T005 can run in parallel
- T006 and T008 can run in parallel after foundations
- T009 and T010 can run in parallel after T007

---

## Parallel Example: User Story 1

```bash
Task: "Implement schedule retrieval service in src/services/ScheduleViewService.java"
Task: "Render schedule display in src/views/final-schedule.html"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. STOP and validate User Story 1 independently
5. Proceed to User Story 2 and 3 after MVP validation
