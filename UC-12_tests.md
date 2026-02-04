## Acceptance Test Suite for UC-12: Submit Paper Review

### AT-01: Submit Completed Review Successfully
**Preconditions**
- Referee is registered and logged in.
- Referee has accepted the review invitation.
- Referee has access to the review form.

**Steps**
1. Referee completes all required review fields.
2. Referee submits the review form.

**Expected Results**
- System validates the review form successfully.
- Review is stored in the CMS database.
- Editor is notified that the review was submitted.

---

### AT-02: Submit Review with Missing or Invalid Required Fields
**Preconditions**
- Referee is logged in and has access to the review form.

**Steps**
1. Referee leaves one or more required fields incomplete or enters invalid data.
2. Referee submits the review form.

**Expected Results**
- System displays an error requesting completion/correction.
- Review is not stored in the database.
- Editor is not notified.

---

### AT-03: Database/Storage Error During Review Submission
**Preconditions**
- Referee completes the review form correctly.
- Simulate database/storage failure during save.

**Steps**
1. Referee submits the completed review form.

**Expected Results**
- System displays an error message and prompts retry.
- Review is not stored in the database.
- Editor is not notified.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and both documented **extension flows**: validation failure due to missing/invalid fields (AT-02) and database/storage failure (AT-03). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-12.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, if the future “submission deadline” flow is added (noted as an open issue), add the following test once the deadline rules are defined:

- **AT-04: Attempt to Submit Review After Submission Deadline (if deadlines are introduced)**
  - **Preconditions:** Review deadline exists and has passed; referee has access to the form.
  - **Steps:** Referee attempts to submit a completed review after the deadline.
  - **Expected Results:** System blocks submission with an appropriate message; review is not stored; editor is not notified.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow in UC-12 (main success or a documented extension). There are no orphan tests, so all current test cases are valid to keep.