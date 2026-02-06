# Tasks: Edit Conference Schedule

**Input**: Design documents from `/specs/UC-16-edit-conference-schedule/`
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
- [ ] T002 [P] Add UC-16 contract file reference to specs/UC-16-edit-conference-schedule/contracts/README.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 [P] Define Schedule model (status, updated_at) in src/models/Schedule.java
- [ ] T004 [P] Define Session model (room_id, time_slot_id, schedule_id) in src/models/Session.java
- [ ] T005 [P] Define Room and TimeSlot models in src/models/Room.java and src/models/TimeSlot.java
- [ ] T006 [P] Create repositories for schedules and sessions in src/services/ScheduleRepository.java and src/services/SessionRepository.java
- [ ] T007 Add schedule edit validation service in src/services/ScheduleValidationService.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Editor edits schedule (Priority: P1) 🎯 MVP

**Goal**: Display editable schedule, accept edits, validate, save, and confirm.

**Independent Test**: Edit schedule and verify save and confirmation.

### Implementation for User Story 1

- [ ] T008 [P] [US1] Implement editable schedule retrieval in src/services/ScheduleEditService.java (depends on T003, T004, T006)
- [ ] T009 [US1] Implement schedule update save flow in src/services/ScheduleEditService.java (depends on T008, T007)
- [ ] T010 [US1] Add edit endpoint per contract in src/controllers/ScheduleEditController.java (depends on T009)
- [ ] T011 [US1] Render editable schedule in src/views/schedule-edit.html (depends on T008)
- [ ] T012 [US1] Confirm update success in src/views/schedule-edit.html (depends on T009)

**Checkpoint**: User Story 1 should now be fully functional and testable independently

---

## Phase 4: User Story 2 - Conflict detection on edits (Priority: P2)

**Goal**: Detect conflicts, highlight inline, and request correction.

**Independent Test**: Create overlap and verify inline highlights and no save.

### Implementation for User Story 2

- [ ] T013 [US2] Detect conflicts in src/services/ScheduleValidationService.java (depends on T007)
- [ ] T014 [US2] Return conflict details in src/controllers/ScheduleEditController.java (depends on T010, T013)
- [ ] T015 [US2] Highlight conflicts inline in src/views/schedule-edit.html (depends on T011, T014)

**Checkpoint**: User Stories 1 and 2 should both work independently

---

## Phase 5: User Story 3 - Validation or save failure (Priority: P3)

**Goal**: Show validation errors with field details and retry-later message on save failure.

**Independent Test**: Submit invalid edits and simulate save error to verify messages and no save.

### Implementation for User Story 3

- [ ] T016 [US3] Return field-level validation errors in src/services/ScheduleValidationService.java (depends on T007)
- [ ] T017 [US3] Map validation errors to response in src/controllers/ScheduleEditController.java (depends on T010, T016)
- [ ] T018 [US3] Show retry-later message on save failure in src/controllers/ScheduleEditController.java (depends on T010, T009)

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T019 [P] Update documentation references in specs/UC-16-edit-conference-schedule/quickstart.md to reflect implementation notes
- [ ] T020 [P] Run quickstart.md validation steps and record findings in specs/UC-16-edit-conference-schedule/quickstart.md

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
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Depends on US1 save flow
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Depends on US1 save flow

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- T003, T004, T005, T006 can run in parallel
- T008 and T011 can run in parallel after foundations
- T013 and T014 can run in parallel after T010

---

## Parallel Example: User Story 1

```bash
Task: "Implement editable schedule retrieval in src/services/ScheduleEditService.java"
Task: "Render editable schedule in src/views/schedule-edit.html"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. STOP and validate User Story 1 independently
5. Proceed to User Story 2 and 3 after MVP validation
