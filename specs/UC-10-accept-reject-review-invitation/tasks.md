# Tasks: Accept or Reject Review Invitation

**Input**: Design documents from `/specs/UC-10-accept-reject-review-invitation/`
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
- [ ] T002 [P] Add UC-10 contract file reference to specs/UC-10-accept-reject-review-invitation/contracts/README.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 [P] Define ReviewInvitation model (status, expires_at, referee_id, paper_id) in src/models/ReviewInvitation.java
- [ ] T004 [P] Define InvitationResponse model (decision, responded_at, invitation_id) in src/models/InvitationResponse.java
- [ ] T005 [P] Add or extend ReviewAssignment model (status) in src/models/ReviewAssignment.java
- [ ] T006 [P] Add or extend Paper and Referee models to include fields used by UC-10 in src/models/Paper.java and src/models/Referee.java
- [ ] T007 [P] Create repository interfaces for invitations and responses in src/services/ReviewInvitationRepository.java and src/services/InvitationResponseRepository.java
- [ ] T008 Create EditorNotificationService interface and stub implementation in src/services/EditorNotificationService.java
- [ ] T009 Add shared validation helpers for invitation status and response locking in src/services/InvitationValidationService.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Referee responds to invitation (Priority: P1) 🎯 MVP

**Goal**: Allow referee to accept or reject, record response, and update assignment status.

**Independent Test**: Submit Accept/Reject on a valid invitation and verify response recorded and assignment status updated.

### Implementation for User Story 1

- [ ] T010 [P] [US1] Implement invitation response submission service in src/services/InvitationResponseService.java (depends on T003, T004, T007, T009)
- [ ] T011 [US1] Update assignment status on response in src/services/ReviewAssignmentService.java (depends on T005, T010)
- [ ] T012 [US1] Add response submission endpoint per contract in src/controllers/InvitationResponseController.java (depends on T010)
- [ ] T013 [US1] Enforce single-response rule in src/services/InvitationResponseService.java (depends on T009, T010)
- [ ] T014 [US1] Block access to expired invitations in src/controllers/ReviewInvitationController.java (depends on T003, T009)

**Checkpoint**: User Story 1 should now be fully functional and testable independently

---

## Phase 4: User Story 2 - Editor notified of response (Priority: P2)

**Goal**: Notify the editor when a response is recorded with decision only.

**Independent Test**: Submit a response and verify editor receives decision-only notification.

### Implementation for User Story 2

- [ ] T015 [US2] Add editor notification calls on response record in src/services/InvitationResponseService.java (depends on T008, T010)
- [ ] T016 [US2] Implement editor notification persistence or dispatch in src/services/EditorNotificationService.java (depends on T008)

**Checkpoint**: User Stories 1 and 2 should both work independently

---

## Phase 5: User Story 3 - Error handling for invalid response attempts (Priority: P3)

**Goal**: Handle expired invitations and database errors with clear messages and no recorded response.

**Independent Test**: Submit on expired invitation and simulate database error to verify messages and no updates.

### Implementation for User Story 3

- [ ] T017 [US3] Return expired invitation error on response submit in src/services/InvitationResponseService.java (depends on T009, T010)
- [ ] T018 [US3] Return database error with retry message in src/services/InvitationResponseService.java (depends on T010)
- [ ] T019 [US3] Ensure no assignment status change on failed response record in src/services/ReviewAssignmentService.java (depends on T011)

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T020 [P] Update documentation references in specs/UC-10-accept-reject-review-invitation/quickstart.md to reflect implementation notes
- [ ] T021 [P] Run quickstart.md validation steps and record findings in specs/UC-10-accept-reject-review-invitation/quickstart.md

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
- T010 and T011 can run in parallel after foundations
- T015 and T016 can run in parallel after T010

---

## Parallel Example: User Story 1

```bash
Task: "Implement invitation response submission service in src/services/InvitationResponseService.java"
Task: "Update assignment status on response in src/services/ReviewAssignmentService.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. STOP and validate User Story 1 independently
5. Proceed to User Story 2 and 3 after MVP validation
