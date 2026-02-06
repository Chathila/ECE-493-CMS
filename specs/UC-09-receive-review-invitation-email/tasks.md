# Tasks: Receive Review Invitation Email

**Input**: Design documents from `/specs/UC-09-receive-review-invitation-email/`
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
- [ ] T002 [P] Add UC-09 contract file reference to specs/UC-09-receive-review-invitation-email/contracts/README.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 [P] Define ReviewInvitation model (status, sent_at, assignment_id, referee_id, paper_id) in src/models/ReviewInvitation.java
- [ ] T004 [P] Define DeliveryFailureRecord model (reason, failed_at, invitation_id) in src/models/DeliveryFailureRecord.java
- [ ] T005 [P] Add or extend Referee, Paper, RefereeAssignment models to include fields used by UC-09 in src/models/Referee.java, src/models/Paper.java, src/models/RefereeAssignment.java
- [ ] T006 [P] Create repository interfaces for invitations and failure records in src/services/ReviewInvitationRepository.java and src/services/DeliveryFailureRepository.java
- [ ] T007 Create EmailDeliveryService interface and stub implementation in src/services/EmailDeliveryService.java
- [ ] T008 Create EditorNotificationService interface and stub implementation in src/services/EditorNotificationService.java
- [ ] T009 Add shared validation helpers for email and duplicate assignment checks in src/services/InvitationValidationService.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Referee receives review invitation (Priority: P1) 🎯 MVP

**Goal**: Generate and send a review invitation with paper details and accept/reject instructions.

**Independent Test**: Assign a referee to a paper and verify an invitation is sent with title, abstract, and instructions.

### Implementation for User Story 1

- [ ] T010 [P] [US1] Implement invitation content builder (title, abstract, instructions) in src/services/InvitationComposer.java
- [ ] T011 [US1] Implement ReviewInvitationService create-and-send flow in src/services/ReviewInvitationService.java (depends on T003, T006, T007, T009, T010)
- [ ] T012 [US1] Suppress duplicate invitations for the same referee-paper assignment in src/services/ReviewInvitationService.java (depends on T009, T011)
- [ ] T013 [US1] Add invitation send endpoint per contract in src/controllers/ReviewInvitationController.java (depends on T011, T012)
- [ ] T014 [US1] Wire invitation send to assignment completion flow in src/services/RefereeAssignmentService.java (depends on T011)

**Checkpoint**: User Story 1 should now be fully functional and testable independently

---

## Phase 4: User Story 2 - Editor notified of delivery failure (Priority: P2)

**Goal**: Notify the editor when delivery fails or when the referee email is invalid.

**Independent Test**: Simulate delivery failure and invalid email to verify editor notifications are generated.

### Implementation for User Story 2

- [ ] T015 [US2] Add editor notification calls for delivery failures in src/services/ReviewInvitationService.java (depends on T008, T011)
- [ ] T016 [US2] Add editor notification calls for invalid email in src/services/ReviewInvitationService.java (depends on T008, T009, T011)
- [ ] T017 [US2] Implement editor notification persistence or dispatch in src/services/EditorNotificationService.java (depends on T008)

**Checkpoint**: User Stories 1 and 2 should both work independently

---

## Phase 5: User Story 3 - Failure recorded for follow-up (Priority: P3)

**Goal**: Record delivery failures with reasons and timestamps for audit and follow-up.

**Independent Test**: Force a failed send and verify a failure record is stored with reason and time.

### Implementation for User Story 3

- [ ] T018 [US3] Implement failure record creation on send failure in src/services/ReviewInvitationService.java (depends on T004, T006, T011)
- [ ] T019 [US3] Persist failure records via repository in src/services/DeliveryFailureRepository.java (depends on T006)
- [ ] T020 [US3] Add failure reason mapping (invalid email, service outage) in src/services/ReviewInvitationService.java (depends on T009, T011)

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T021 [P] Update documentation references in specs/UC-09-receive-review-invitation-email/quickstart.md to reflect implementation notes
- [ ] T022 [P] Run quickstart.md validation steps and record findings in specs/UC-09-receive-review-invitation-email/quickstart.md

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

- T003, T004, T005, T006 can run in parallel
- T010 can run in parallel with T011 preparation
- T015 and T016 can run in parallel after T011

---

## Parallel Example: User Story 1

```bash
Task: "Implement invitation content builder in src/services/InvitationComposer.java"
Task: "Implement ReviewInvitationService create-and-send flow in src/services/ReviewInvitationService.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. STOP and validate User Story 1 independently
5. Proceed to User Story 2 and 3 after MVP validation
