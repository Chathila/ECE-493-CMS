## Acceptance Test Suite for UC-11: Access Paper Review Form

### AT-01: Access Review Form Successfully
**Preconditions**
- Referee is registered and logged in.
- Paper is assigned to the referee.
- Referee has accepted the review invitation.

**Steps**
1. Referee opens the assigned papers list.
2. Referee selects an assigned paper.

**Expected Results**
- System authorizes the referee for the selected paper.
- Review form and paper details are retrieved.
- Review form is displayed to the referee.

---

### AT-02: Access Denied for Unauthorized Paper
**Preconditions**
- Referee is logged in.
- Paper is not assigned to the referee (or invitation not accepted).

**Steps**
1. Referee attempts to open the review form for the unassigned paper.

**Expected Results**
- System displays an access-denied error message.
- Review form is not displayed.

---

### AT-03: Review Form Retrieval Failure (System/Database Error)
**Preconditions**
- Referee is logged in and authorized for the paper.
- Simulate system/database failure when retrieving the review form.

**Steps**
1. Referee selects an assigned paper to review.

**Expected Results**
- System displays an error indicating the review form is unavailable.
- Review form is not displayed.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and both documented **extension flows**: unauthorized access (AT-02) and retrieval failure due to system/database error (AT-03). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-11.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, the use case analysis suggested explicitly handling the precondition “invitation accepted.” AT-02 partially covers this by including “invitation not accepted” as a reason for denial, but it is combined with “paper not assigned.” If you want clearer flow coverage, add a separate test that isolates this condition:

- **AT-04: Access Denied When Invitation Not Accepted (explicit precondition enforcement)**
  - **Preconditions:** Referee is logged in; paper is assigned but invitation status is still pending/not accepted.
  - **Steps:** Referee selects the paper from their list (or attempts direct access to the form).
  - **Expected Results:** System denies access and explains that invitation must be accepted before reviewing; form is not displayed.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow in UC-11 (main success or a documented extension). AT-02 is still valid to keep even though it combines two possible causes (unassigned vs not accepted), because both represent the same “not authorized” flow from the user’s perspective.