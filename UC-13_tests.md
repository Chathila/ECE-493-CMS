## Acceptance Test Suite for UC-13: Make Final Paper Decision

### AT-01: Record Acceptance Decision Successfully
**Preconditions**
- Editor is registered and logged in.
- Paper has all required reviews submitted.
- Review period is closed for the paper.

**Steps**
1. Editor selects a paper awaiting decision.
2. Editor selects **Accept**.
3. Editor submits the final decision.

**Expected Results**
- System validates all required reviews are present.
- Decision is recorded in the CMS database.
- Author is notified of acceptance.

---

### AT-02: Record Rejection Decision Successfully
**Preconditions**
- Editor is registered and logged in.
- Paper has all required reviews submitted.
- Review period is closed for the paper.

**Steps**
1. Editor selects a paper awaiting decision.
2. Editor selects **Reject**.
3. Editor submits the final decision.

**Expected Results**
- System validates all required reviews are present.
- Decision is recorded in the CMS database.
- Author is notified of rejection.

---

### AT-03: Attempt Decision Before All Reviews Completed
**Preconditions**
- Editor is logged in.
- Paper is missing one or more required reviews.

**Steps**
1. Editor selects the paper.
2. Editor attempts to submit **Accept** or **Reject**.

**Expected Results**
- System displays an error indicating reviews are incomplete.
- Decision is not recorded in the database.
- Author is not notified.

---

### AT-04: Database Error While Recording Decision
**Preconditions**
- Editor is logged in.
- Paper has all required reviews submitted.
- Simulate database failure during decision save.

**Steps**
1. Editor submits **Accept** or **Reject** decision.

**Expected Results**
- System displays an error and requests retry.
- Decision is not recorded in the database.
- Author is not notified.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** for both decision branches (Accept in AT-01 and Reject in AT-02) and both documented **extension flows**: attempting a decision before all reviews are complete (AT-03) and database failure while recording the decision (AT-04). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-13.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, the UC-13 use case analysis suggested an additional operational flow: “decision recorded but author notification fails.” If that flow is added to the use case, include:

- **AT-05: Decision Recorded but Author Notification Fails (Notification Service Error)**
  - **Preconditions:** Editor is logged in; all reviews are complete; simulate notification/email failure after decision is saved.
  - **Steps:** Editor submits an Accept/Reject decision.
  - **Expected Results:** Decision is recorded in the database; system logs notification failure; editor is informed; author is not notified via the failed channel.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow in UC-13 (Accept branch, Reject branch, incomplete reviews extension, database error extension). There are no orphan tests, so all current test cases are valid to keep.