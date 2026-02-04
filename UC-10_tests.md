## Acceptance Test Suite for UC-10: Accept or Reject Review Invitation

### AT-01: Accept Invitation Successfully
**Preconditions**
- Referee is registered in CMS.
- Referee has a valid, unexpired review invitation.

**Steps**
1. Referee opens the invitation.
2. Referee selects **Accept**.
3. Referee submits the response.

**Expected Results**
- System records the **Accept** response in the CMS database.
- Review assignment status is updated to accepted.
- Editor is notified of the referee’s decision.

---

### AT-02: Reject Invitation Successfully
**Preconditions**
- Referee is registered in CMS.
- Referee has a valid, unexpired review invitation.

**Steps**
1. Referee opens the invitation.
2. Referee selects **Reject**.
3. Referee submits the response.

**Expected Results**
- System records the **Reject** response in the CMS database.
- Review assignment status is updated to rejected.
- Editor is notified of the referee’s decision.

---

### AT-03: Attempt to Respond After Invitation Expired
**Preconditions**
- Referee has an invitation that is expired/closed for response.

**Steps**
1. Referee opens the invitation.
2. Referee selects **Accept** or **Reject**.
3. Referee submits the response.

**Expected Results**
- System displays an error indicating the invitation is no longer valid.
- No response is recorded in the database.
- Assignment status is not changed.

---

### AT-04: Database Error When Recording Response
**Preconditions**
- Referee has a valid invitation.
- Simulate database error on response save.

**Steps**
1. Referee opens the invitation.
2. Referee selects **Accept** or **Reject**.
3. Referee submits the response.

**Expected Results**
- System displays an error message and prompts the referee to retry.
- Response is not recorded in the database.
- Assignment status is not changed.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** for both decision branches (Accept in AT-01 and Reject in AT-02) and both documented **extension flows**: responding after expiration (AT-03) and database failure when saving the response (AT-04). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-10.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. If you add the previously suggested “abandon without responding” flow to the use case, add the following test:

- **AT-05: Referee Opens Invitation but Does Not Submit a Response**
  - **Preconditions:** Referee has a valid invitation.
  - **Steps:** Referee opens the invitation and closes/leaves without selecting Accept/Reject and submitting.
  - **Expected Results:** No response is recorded; invitation remains pending; assignment status is unchanged.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow in UC-10 (Accept branch, Reject branch, expired invitation extension, database error extension). There are no orphan tests, so all current test cases are valid to keep.