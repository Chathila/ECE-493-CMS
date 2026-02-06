# Feature Specification: Assign Referees to Submitted Papers

**Feature Branch**: `UC-07-assign-referees`  
**Created**: 2026-02-05  
**Status**: Draft  
**Input**: User description: "Assign referees to submitted papers from US-07_use_case.md"

## Clarifications

### Session 2026-02-05

- Q: What is the minimum number of referees required per paper? → A: At least one referee.
- Q: Is there a maximum number of referees per paper? → A: No maximum.
- Q: What happens if the notification service is unavailable? → A: Assign referees, but show a warning that invitations were not sent.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Assign Referees Successfully (Priority: P1)

An editor assigns eligible referees to a submitted paper so reviews can begin.

**Why this priority**: Assignments are required for the review workflow.

**Independent Test**: Can be fully tested by selecting valid referees within
limits and verifying assignments plus notifications.

**Acceptance Scenarios**:

1. **Given** a submitted paper and available referees, **When** the editor selects
   eligible referees within limits and submits, **Then** the system assigns them,
   stores the assignments, and sends invitations.

---

### User Story 2 - Handle Invalid Referee Selection (Priority: P2)

An editor receives clear feedback when a selected referee email is invalid.

**Why this priority**: Prevents assigning non-existent referees.

**Independent Test**: Can be tested by entering an invalid referee email and
verifying an error message with no assignment stored.

**Acceptance Scenarios**:

1. **Given** the assignment interface, **When** the editor enters a referee email
   not in the system, **Then** the system displays an invalid referee email
   error and does not assign.

---

### User Story 3 - Enforce Assignment Limits (Priority: P3)

An editor receives clear feedback when referee workload or per-paper limits are
exceeded.

**Why this priority**: Enforces conference assignment rules.

**Independent Test**: Can be tested by selecting an overworked referee or too
many referees and verifying workload/limit errors.

**Acceptance Scenarios**:

1. **Given** the assignment interface, **When** the editor selects a referee who
   has exceeded the maximum assignments, **Then** the system shows a workload
   violation message and prevents assignment.
2. **Given** the assignment interface, **When** the editor selects more than the
   allowed number of referees, **Then** the system shows a referee limit error
   and prevents assignment.

---

### Edge Cases

- What happens when fewer than the required number of referees are selected?
- What happens when the notification service is unavailable?
- What happens when the assignment page is unavailable or errors on load?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide an option to assign referees for submitted
  papers.
- **FR-002**: System MUST display an assignment interface for selecting referees
  by email.
- **FR-003**: System MUST verify referee emails exist in the system.
- **FR-004**: System MUST enforce referee workload limits.
- **FR-005**: System MUST require at least one referee per paper.
- **FR-005a**: System MUST allow more than one referee per paper with no maximum.
- **FR-006**: System MUST assign selected referees when validations pass.
- **FR-007**: System MUST store referee assignments in the CMS database.
- **FR-008**: System MUST send review invitation notifications to assigned
  referees.
- **FR-009**: System MUST display an error message for invalid referee emails.
- **FR-010**: System MUST display an error message for workload violations.
- **FR-011**: System MUST display an error message for exceeding referee limits.
- **FR-012**: System MUST allow assignments to be stored even if notifications
  fail, and display a warning that invitations were not sent.

### Key Entities *(include if feature involves data)*

- **Referee Assignment**: Represents a referee assigned to a paper.
- **Referee**: Represents a reviewer with workload metadata.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Referee assignment completes within 2 minutes end-to-end.
- **SC-002**: At least 95% of valid assignments are stored successfully on the
  first attempt.
- **SC-003**: At least 99% of invalid referee emails are rejected with a clear
  error message.
- **SC-004**: At least 99% of workload or limit violations are blocked with a
  clear error message.

## Assumptions

- Referee workload limits and per-paper referee limits are defined by CMS policy.
- Notification service is available during standard operation.
