# Feature Specification: Make Final Paper Decision

**Feature Branch**: `UC-13-final-paper-decision`  
**Created**: 2026-02-06  
**Status**: Draft  
**Input**: User description: "UC-13: Make Final Paper Decision"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Editor records final decision (Priority: P1)

An editor views completed reviews for a paper, selects Accept or Reject, and
submits the decision so it is recorded and the author is notified.

**Why this priority**: The final decision is required to close the review
process and notify the author.

**Independent Test**: Can be tested by selecting a paper with completed reviews,
submitting Accept/Reject, and verifying the decision is stored and the author is
notified.

**Acceptance Scenarios**:

1. **Given** a paper with all required reviews completed, **When** the editor
   submits an Accept or Reject decision, **Then** the system validates the
   reviews, records the decision, and notifies the author.

---

### User Story 2 - Reviews incomplete error (Priority: P2)

If an editor attempts to decide before all required reviews are complete, the
system blocks the decision and displays an error.

**Why this priority**: Prevents premature decisions and ensures review
completeness.

**Independent Test**: Can be tested by selecting a paper with incomplete reviews
and verifying the system blocks the decision and shows the error.

**Acceptance Scenarios**:

1. **Given** a paper with missing required reviews, **When** the editor attempts
   to submit a decision, **Then** the system displays a reviews-incomplete error
   and does not record the decision.

---

### User Story 3 - Decision recording failure (Priority: P3)

If the system fails to record the decision due to a database error, the system
shows an error and requests a retry.

**Why this priority**: Provides clear feedback when persistence fails.

**Independent Test**: Can be tested by simulating a database error and verifying
an error message is shown and the decision is not recorded.

**Acceptance Scenarios**:

1. **Given** a valid decision submission, **When** the system encounters a
   database error, **Then** the system displays a retry message and does not
   record the decision.

---

### Edge Cases

- What happens when the editor attempts a decision before reviews are complete?
  The system displays a reviews-incomplete error and does not record the
  decision.
- How does the system handle a database error while recording the decision? The
  system displays a retry message and does not record the decision.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST display submitted reviews and evaluation details for a
  selected paper awaiting final decision.
- **FR-002**: Editors MUST be able to select Accept or Reject for the final
  decision.
- **FR-003**: System MUST validate that all required reviews are present before
  recording a final decision.
- **FR-004**: System MUST record the editor's final decision in the CMS
  database.
- **FR-005**: System MUST notify the author when a final decision is
  successfully recorded.
- **FR-006**: System MUST display a reviews-incomplete error and block decision
  submission when required reviews are missing.
- **FR-007**: System MUST display a retry message and not record the decision
  when a database error occurs.
- **FR-008**: Author notifications MUST include only the accept/reject decision.
- **FR-009**: When reviews are incomplete, the system MUST show the error on
  the decision page and block submission.
- **FR-010**: Retry messages MUST instruct the editor to retry later.

### Key Entities *(include if feature involves data)*

- **Final Decision**: Represents the editor's Accept/Reject decision for a
  paper.
- **Review**: Represents completed review submissions used in the decision.
- **Paper**: Represents the submitted paper under decision.
- **Editor**: Represents the decision-making user.
- **Author**: Represents the recipient of the decision notification.

## Assumptions

- The editor is logged in and the review period is closed for the paper.
- The system can determine whether all required reviews are complete.

## Clarifications

### Session 2026-02-06

- Q: What content should be included in author notifications? → A: Include only
  the accept/reject decision.
- Q: Where should the reviews-incomplete error be shown? → A: On the decision
  page, blocking submission.
- Q: What should the retry message instruct the editor to do? → A: Retry later.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of valid decisions are recorded and trigger author
  notification.
- **SC-002**: 100% of decision attempts with incomplete reviews display a
  reviews-incomplete error and do not record a decision.
- **SC-003**: 100% of database errors display a retry message and do not record
  a decision.
