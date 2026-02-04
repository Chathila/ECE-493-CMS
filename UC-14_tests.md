## Acceptance Test Suite for UC-14: Receive Final Paper Decision

### AT-01: Author Receives Decision Notification Successfully
**Preconditions**
- Author is registered with valid contact information.
- Final decision is recorded for the paper.

**Steps**
1. Editor submits the final decision for the paper.
2. System sends the decision notification.

**Expected Results**
- Author receives the decision notification (accept/reject).
- Decision status is accessible in the CMS.

---

### AT-02: Notification Delivery Failure
**Preconditions**
- Final decision is recorded for the paper.
- Simulate email/system notification failure.

**Steps**
1. Editor submits the final decision.
2. System attempts to send the decision notification.

**Expected Results**
- System logs the delivery failure.
- System notifies the editor of the issue.
- Author does not receive the notification.

---

### AT-03: Author Views Decision Before Notification Delivery
**Preconditions**
- Final decision is recorded for the paper.
- Notification has not yet been delivered.

**Steps**
1. Author logs in to the CMS.
2. Author navigates to the paper status page.

**Expected Results**
- System displays the final decision status directly from the CMS database.
- Author can see the paper decision even without the notification.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and both documented **extension flows**: notification delivery failure (AT-02) and author viewing the decision before notification delivery (AT-03). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-14.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required for the documented flows. However, UC-14 has a precondition that the author has valid contact information. If you want explicit coverage of that precondition being violated (often treated as an extension in practice), add:

- **AT-04: Missing/Invalid Author Contact Information**
  - **Preconditions:** Final decision recorded; author contact info is missing/invalid in CMS.
  - **Steps:** System attempts to send the decision notification.
  - **Expected Results:** System logs the contact info issue; editor is alerted; author does not receive notification; decision remains viewable in CMS.

### (b) Tests that do not map to a specific flow
All existing tests map to a specific flow in UC-14 (main success or a documented extension). There are no orphan tests, so all current test cases are valid to keep. AT-03 is especially worth keeping because it verifies the requirement that the decision status is accessible in the CMS even if delivery is delayed or fails.
