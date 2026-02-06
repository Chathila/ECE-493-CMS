# Tasks: Receive Final Paper Decision

**Input**: Design documents from `/specs/UC-14-receive-final-decision/`
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
- [ ] T002 [P] Add UC-14 contract file reference to specs/UC-14-receive-final-decision/contracts/README.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 [P] Define FinalDecision model (decision, recorded_at, paper_id) in src/models/FinalDecision.java
- [ ] T004 [P] Add or extend Paper model (title, status) in src/models/Paper.java
- [ ] T005 [P] Add or extend Author and Editor models (email) in src/models/Author.java and src/models/Editor.java
- [ ] T006 [P] Define NotificationDeliveryFailure model (decision_id, channel, failed_at, message) in src/models/NotificationDeliveryFailure.java
- [ ] T007 [P] Create repositories for decisions and delivery failures in src/services/FinalDecisionRepository.java and src/services/NotificationFailureRepository.java
- [ ] T008 Create NotificationService interface for email/in-system delivery in src/services/NotificationService.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Author receives decision notification (Priority: P1) 🎯 MVP

**Goal**: Notify the author via email and in-system channels and ensure decision status is visible.

**Independent Test**: Record a decision and verify author receives notification and can view status.

### Implementation for User Story 1

- [ ] T009 [P] [US1] Implement notification dispatch for email and in-system channels in src/services/NotificationService.java (depends on T008)
- [ ] T010 [US1] Trigger notification on decision record in src/services/FinalDecisionNotificationService.java (depends on T003, T007, T009)
- [ ] T011 [US1] Add notification trigger endpoint per contract in src/controllers/DecisionNotificationController.java (depends on T010)
- [ ] T012 [US1] Implement decision status retrieval in src/controllers/DecisionStatusController.java (depends on T003, T004)
- [ ] T013 [US1] Add decision status view in src/views/decision-status.html (depends on T012)

**Checkpoint**: User Story 1 should now be fully functional and testable independently

---

## Phase 4: User Story 2 - Notification delivery failure (Priority: P2)

**Goal**: Log delivery failures and notify editor with minimal details.

**Independent Test**: Simulate a delivery failure and verify logging and editor notification.

### Implementation for User Story 2

- [ ] T014 [US2] Log notification delivery failures in src/services/NotificationFailureRepository.java (depends on T006, T007)
- [ ] T015 [US2] Notify editor of delivery failure in src/services/EditorFailureNotificationService.java (depends on T005)
- [ ] T016 [US2] Wire failure handling into notification flow in src/services/NotificationService.java (depends on T009, T014, T015)

**Checkpoint**: User Stories 1 and 2 should both work independently

---

## Phase 5: User Story 3 - Author views decision before notification (Priority: P3)

**Goal**: Display decision status immediately after decision recording.

**Independent Test**: Access decision status before delivery and verify status is visible.

### Implementation for User Story 3

- [ ] T017 [US3] Ensure decision status read path does not depend on notification delivery in src/services/DecisionStatusService.java (depends on T003)
- [ ] T018 [US3] Return immediate decision status in src/controllers/DecisionStatusController.java (depends on T012, T017)

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T019 [P] Update documentation references in specs/UC-14-receive-final-decision/quickstart.md to reflect implementation notes
- [ ] T020 [P] Run quickstart.md validation steps and record findings in specs/UC-14-receive-final-decision/quickstart.md

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
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Depends on US1 notification flow
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Depends on US1 decision status flow

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- T003, T004, T005, T006, T007 can run in parallel
- T009 and T012 can run in parallel after foundations
- T014 and T015 can run in parallel after T010

---

## Parallel Example: User Story 1

```bash
Task: "Implement notification dispatch in src/services/NotificationService.java"
Task: "Implement decision status view in src/views/decision-status.html"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. STOP and validate User Story 1 independently
5. Proceed to User Story 2 and 3 after MVP validation
