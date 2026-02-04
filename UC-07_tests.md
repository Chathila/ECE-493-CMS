## Acceptance Test Suite for UC-07: Assign Referees to Submitted Papers

### AT-01: Successfully Assign Referees and Send Invitations
**Preconditions**
- Editor is registered and logged in.
- Submitted paper exists.
- Referee emails exist in the system and are eligible (under workload limit).

**Steps**
1. Editor selects a submitted paper.
2. Editor enters/selects referee email addresses.
3. Editor submits the assignment.

**Expected Results**
- System assigns referees to the paper.
- Assignments are stored in the CMS database.
- Review invitation notifications are sent to assigned referees.

---

### AT-02: Invalid Referee Email Address
**Preconditions**
- Editor is logged in.
- Submitted paper exists.

**Steps**
1. Editor enters a referee email that does not exist in the system.
2. Editor submits the assignment.

**Expected Results**
- System displays an error indicating invalid referee email.
- Referee is not assigned and no invitation is sent for that email.

---

### AT-03: Referee Workload Limit Exceeded
**Preconditions**
- Editor is logged in.
- Submitted paper exists.
- Selected referee already has the maximum allowed assigned papers.

**Steps**
1. Editor attempts to assign the overloaded referee to the paper.
2. Editor submits the assignment.

**Expected Results**
- System displays a workload violation message.
- Assignment is prevented for that referee.

---

### AT-04: Too Many Referees Assigned to a Paper
**Preconditions**
- Editor is logged in.
- Submitted paper exists.

**Steps**
1. Editor selects more referees than the allowed limit for a paper.
2. Editor submits the assignment.

**Expected Results**
- System displays an error indicating the referee limit per paper.
- System prevents saving the invalid assignment set.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and all documented **extension flows**: invalid referee email (AT-02), workload cap exceeded (AT-03), and exceeding the referee limit per paper (AT-04). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-07.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, the use case analysis proposed an additional flow for assigning fewer than the required minimum number of referees (if conference rules require a minimum). If that rule is part of the requirements, add:

- **AT-05: Too Few Referees Assigned to a Paper (if a minimum is required)**
  - **Preconditions:** Editor is logged in; submitted paper exists; minimum referees required is defined.
  - **Steps:** Editor selects fewer than the minimum required referees; submits assignment.
  - **Expected Results:** System displays an error indicating insufficient referees; assignment is not saved and invitations are not sent.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow (main success or a documented extension). There are no tests that fail to cover a defined flow, so all current tests are valid to keep.