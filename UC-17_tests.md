## Acceptance Test Suite for UC-17: View Final Conference Schedule

### AT-01: View Published Final Schedule Successfully
**Preconditions**
- Final conference schedule is published.
- User can access the CMS website.

**Steps**
1. User navigates to the conference schedule section.
2. User selects **View Final Schedule**.

**Expected Results**
- System retrieves the published schedule from the database.
- Schedule is displayed in a readable format.

---

### AT-02: Final Schedule Not Published
**Preconditions**
- Final schedule is not published.

**Steps**
1. User navigates to the conference schedule section.
2. User selects **View Final Schedule**.

**Expected Results**
- System displays a message indicating the schedule is not available.
- No schedule content is shown.

---

### AT-03: Database Error During Schedule Retrieval
**Preconditions**
- Final schedule exists (published or intended to be available).
- Simulate database retrieval failure.

**Steps**
1. User selects **View Final Schedule**.

**Expected Results**
- System displays an error message requesting the user to try again later.
- Schedule is not displayed.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and both documented **extension flows**: schedule not yet published (AT-02) and database retrieval failure (AT-03). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-17.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, UC-17 has an open issue about “public access permissions.” If permissions become a defined flow, add:

- **AT-04: Access Denied Due to Permissions (if schedule viewing becomes restricted)**
  - **Preconditions:** Schedule is published; user is not permitted to view it (per policy).
  - **Steps:** User attempts to view the final schedule.
  - **Expected Results:** System denies access and displays an authorization/permission message; schedule is not displayed.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow in UC-17 (main success or a documented extension). There are no orphan tests, so all current test cases are valid to keep.