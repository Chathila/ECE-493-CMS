# Tasks: Pay Conference Registration Fee

**Input**: Design documents from `/specs/UC-19-pay-registration-fee/`
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
- [ ] T002 [P] Add UC-19 contract file reference to specs/UC-19-pay-registration-fee/contracts/README.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 [P] Define Payment model (registration_type, amount, status, confirmed_at) in src/models/Payment.java
- [ ] T004 [P] Define RegistrationType model (name, price) in src/models/RegistrationType.java
- [ ] T005 [P] Create repository for payments in src/services/PaymentRepository.java
- [ ] T006 Add payment service adapter in src/services/PaymentService.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Attendee pays registration fee (Priority: P1) 🎯 MVP

**Goal**: Validate details, process payment, record confirmation, and show success message.

**Independent Test**: Submit valid payment and verify confirmation recorded and success message displayed.

### Implementation for User Story 1

- [ ] T007 [P] [US1] Implement payment processing flow in src/services/PaymentProcessingService.java (depends on T003, T005, T006)
- [ ] T008 [US1] Record payment confirmation in src/services/PaymentRepository.java (depends on T005, T007)
- [ ] T009 [US1] Display payment confirmation message in src/views/payment-confirmation.html (depends on T008)
- [ ] T010 [US1] Add payment endpoint per contract in src/controllers/PaymentController.java (depends on T007)

**Checkpoint**: User Story 1 should now be fully functional and testable independently

---

## Phase 4: User Story 2 - Invalid payment details (Priority: P2)

**Goal**: Show correction message when payment details are invalid.

**Independent Test**: Submit invalid details and verify error and no processing.

### Implementation for User Story 2

- [ ] T011 [US2] Validate payment details and return correction errors in src/services/PaymentProcessingService.java (depends on T007)
- [ ] T012 [US2] Render correction message in src/views/payment-confirmation.html (depends on T009, T011)

**Checkpoint**: User Stories 1 and 2 should both work independently

---

## Phase 5: User Story 3 - Payment service failure (Priority: P3)

**Goal**: Handle declined payments and service outages with retry guidance.

**Independent Test**: Simulate decline and outage and verify messages and retry guidance.

### Implementation for User Story 3

- [ ] T013 [US3] Handle declined payments with retry option in src/services/PaymentProcessingService.java (depends on T007, T011)
- [ ] T014 [US3] Handle service-unavailable cases with retry-later message in src/services/PaymentProcessingService.java (depends on T007)
- [ ] T015 [US3] Render decline and service-unavailable messages in src/views/payment-confirmation.html (depends on T009, T013, T014)

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T016 [P] Update documentation references in specs/UC-19-pay-registration-fee/quickstart.md to reflect implementation notes
- [ ] T017 [P] Run quickstart.md validation steps and record findings in specs/UC-19-pay-registration-fee/quickstart.md

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
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Depends on US1 processing flow
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Depends on US1 processing flow

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- T003, T004, T005 can run in parallel
- T007 and T009 can run in parallel after foundations
- T013 and T014 can run in parallel after T007

---

## Parallel Example: User Story 1

```bash
Task: "Implement payment processing flow in src/services/PaymentProcessingService.java"
Task: "Display payment confirmation message in src/views/payment-confirmation.html"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. STOP and validate User Story 1 independently
5. Proceed to User Story 2 and 3 after MVP validation
