# Tasks: Receive Registration Payment Confirmation Ticket

**Input**: Design documents from `/specs/UC-20-payment-confirmation-ticket/`
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
- [ ] T002 [P] Add UC-20 contract file reference to specs/UC-20-payment-confirmation-ticket/contracts/README.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 [P] Define ConfirmationTicket model (payment_id, issued_at) in src/models/ConfirmationTicket.java
- [ ] T004 [P] Define PaymentConfirmation model (amount, confirmed_at) in src/models/PaymentConfirmation.java
- [ ] T005 [P] Define TicketDeliveryFailure model (ticket_id, channel, failed_at, message) in src/models/TicketDeliveryFailure.java
- [ ] T006 [P] Create repositories for tickets and failures in src/services/TicketRepository.java and src/services/TicketFailureRepository.java
- [ ] T007 Create TicketDeliveryService interface for email/in-system delivery in src/services/TicketDeliveryService.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Attendee receives confirmation ticket (Priority: P1) 🎯 MVP

**Goal**: Generate, store, and deliver confirmation ticket after payment.

**Independent Test**: Complete payment and verify ticket generated, stored, and delivered.

### Implementation for User Story 1

- [ ] T008 [P] [US1] Implement ticket generation in src/services/TicketService.java (depends on T003, T004, T006)
- [ ] T009 [US1] Store generated ticket in src/services/TicketRepository.java (depends on T006, T008)
- [ ] T010 [US1] Deliver ticket via email and in-system in src/services/TicketDeliveryService.java (depends on T007, T008)
- [ ] T011 [US1] Add ticket generation endpoint per contract in src/controllers/TicketController.java (depends on T008)

**Checkpoint**: User Story 1 should now be fully functional and testable independently

---

## Phase 4: User Story 2 - Ticket delivery failure (Priority: P2)

**Goal**: Log delivery failures and inform attendee that ticket is available in CMS.

**Independent Test**: Simulate delivery failure and verify logging and attendee message.

### Implementation for User Story 2

- [ ] T012 [US2] Log delivery failures in src/services/TicketFailureRepository.java (depends on T005, T006)
- [ ] T013 [US2] Inform attendee of delivery failure in src/views/ticket-status.html (depends on T010)
- [ ] T014 [US2] Wire failure handling into delivery flow in src/services/TicketDeliveryService.java (depends on T010, T012)

**Checkpoint**: User Stories 1 and 2 should both work independently

---

## Phase 5: User Story 3 - Attendee accesses ticket before delivery (Priority: P3)

**Goal**: Allow view and download of ticket from CMS before delivery.

**Independent Test**: Access ticket before delivery and verify view and download.

### Implementation for User Story 3

- [ ] T015 [US3] Implement ticket retrieval for CMS access in src/services/TicketService.java (depends on T006, T008)
- [ ] T016 [US3] Add ticket view/download endpoint in src/controllers/TicketController.java (depends on T011, T015)
- [ ] T017 [US3] Render ticket view/download options in src/views/ticket-status.html (depends on T016)

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T018 [P] Update documentation references in specs/UC-20-payment-confirmation-ticket/quickstart.md to reflect implementation notes
- [ ] T019 [P] Run quickstart.md validation steps and record findings in specs/UC-20-payment-confirmation-ticket/quickstart.md

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
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Depends on US1 delivery flow
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Depends on US1 ticket generation

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- T003, T004, T005, T006 can run in parallel
- T008 and T010 can run in parallel after foundations
- T012 and T013 can run in parallel after T010

---

## Parallel Example: User Story 1

```bash
Task: "Implement ticket generation in src/services/TicketService.java"
Task: "Deliver ticket via email/in-system in src/services/TicketDeliveryService.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. STOP and validate User Story 1 independently
5. Proceed to User Story 2 and 3 after MVP validation
