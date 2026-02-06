# Tasks: Generate Conference Schedule

**Input**: Design documents from `/specs/UC-15-generate-conference-schedule/`
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
- [ ] T002 [P] Add UC-15 contract file reference to specs/UC-15-generate-conference-schedule/contracts/README.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 [P] Define Schedule model (generated_at, generated_by) in src/models/Schedule.java
- [ ] T004 [P] Define Session model (room_id, time_slot_id, schedule_id) in src/models/Session.java
- [ ] T005 [P] Define AcceptedPaper model (paper_id, title) in src/models/AcceptedPaper.java
- [ ] T006 [P] Define Room and TimeSlot models in src/models/Room.java and src/models/TimeSlot.java
- [ ] T007 [P] Create repositories for schedule data in src/services/ScheduleRepository.java, src/services/SessionRepository.java, src/services/SchedulingDataRepository.java
- [ ] T008 Add scheduling algorithm interface in src/services/SchedulingAlgorithm.java
- [ ] T009 Add schedule generation service in src/services/ScheduleGenerationService.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Administrator generates schedule (Priority: P1) 🎯 MVP

**Goal**: Generate, store, and display a valid schedule for the requesting administrator.

**Independent Test**: Request generation and verify schedule is stored and displayed to the administrator only.

### Implementation for User Story 1

- [ ] T010 [P] [US1] Implement schedule generation flow in src/services/ScheduleGenerationService.java (depends on T003, T004, T005, T006, T007, T008)
- [ ] T011 [US1] Store generated schedule in src/services/ScheduleRepository.java (depends on T010)
- [ ] T012 [US1] Add generation endpoint per contract in src/controllers/ScheduleGenerationController.java (depends on T010)
- [ ] T013 [US1] Display generated schedule for requesting administrator in src/views/schedule.html (depends on T012)

**Checkpoint**: User Story 1 should now be fully functional and testable independently

---

## Phase 4: User Story 2 - Missing scheduling data error (Priority: P2)

**Goal**: Block generation and show missing-data error identifying missing rooms or time slots.

**Independent Test**: Remove required data and verify specific missing-data error with no schedule.

### Implementation for User Story 2

- [ ] T014 [US2] Detect missing rooms or time slots in src/services/SchedulingDataRepository.java (depends on T006, T007)
- [ ] T015 [US2] Return missing-data error with specifics in src/controllers/ScheduleGenerationController.java (depends on T012, T014)

**Checkpoint**: User Stories 1 and 2 should both work independently

---

## Phase 5: User Story 3 - Scheduling algorithm failure (Priority: P3)

**Goal**: Display error and log failure when algorithm cannot produce a valid schedule.

**Independent Test**: Force algorithm failure and verify error and failure log.

### Implementation for User Story 3

- [ ] T016 [US3] Handle algorithm failure and log error in src/services/ScheduleGenerationService.java (depends on T008, T010)
- [ ] T017 [US3] Return scheduling failure error in src/controllers/ScheduleGenerationController.java (depends on T012, T016)

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T018 [P] Update documentation references in specs/UC-15-generate-conference-schedule/quickstart.md to reflect implementation notes
- [ ] T019 [P] Run quickstart.md validation steps and record findings in specs/UC-15-generate-conference-schedule/quickstart.md

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
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Depends on US1 generation flow
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Depends on US1 generation flow

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- T003, T004, T005, T006, T007 can run in parallel
- T010 and T013 can run in parallel after foundations
- T014 and T015 can run in parallel after T012

---

## Parallel Example: User Story 1

```bash
Task: "Implement schedule generation flow in src/services/ScheduleGenerationService.java"
Task: "Display generated schedule in src/views/schedule.html"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. STOP and validate User Story 1 independently
5. Proceed to User Story 2 and 3 after MVP validation
