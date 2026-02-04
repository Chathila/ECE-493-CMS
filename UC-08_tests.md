## Acceptance Test Suite for UC-08: Enforce Reviewer Workload Limit

### AT-01: Allow Assignment When Reviewer Has Fewer Than Five Papers
**Preconditions**
- Editor is registered and logged in.
- Paper is assignable.
- Reviewer exists and currently has fewer than 5 assigned papers.

**Steps**
1. Editor selects a submitted paper.
2. Editor selects the reviewer and submits the assignment.

**Expected Results**
- System allows the assignment.
- Reviewer’s assigned-paper count is updated in the CMS database.
- System confirms the reviewer was assigned.

---

### AT-02: Block Assignment When Reviewer Already Has Five Papers
**Preconditions**
- Editor is logged in.
- Paper is assignable.
- Reviewer exists and currently has exactly 5 assigned papers.

**Steps**
1. Editor selects the reviewer and attempts to assign them to a paper.
2. Editor submits the assignment.

**Expected Results**
- System blocks the assignment.
- Workload violation message is displayed.
- Reviewer’s assigned-paper count is not changed.

---

### AT-03: Workload Count Retrieval Failure (Database Error)
**Preconditions**
- Editor is logged in.
- Simulate database failure when retrieving reviewer workload.

**Steps**
1. Editor attempts to assign a reviewer to a paper.
2. Editor submits the assignment.

**Expected Results**
- System displays an error indicating workload check could not be completed.
- System prevents the assignment.
- No changes are written to the database.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and both documented **extension flows**: workload limit exceeded (AT-02) and failure to retrieve workload information due to a system/database error (AT-03). As a result, the suite achieves **complete flow coverage** for all flows defined in UC-08.

### (a) Missing-flow tests to achieve flow coverage
No additional test cases are required to cover the documented flows. All success and failure paths specified in the use case are already exercised by the current acceptance tests.

Optionally, if future versions allow the workload limit to be configurable, a new test could be added to verify behavior when the limit value changes. However, this is outside the current use case scope.

### (b) Tests that do not map to a specific flow
All acceptance tests directly correspond to a specific flow in the use case:
- AT-01 → Main success scenario  
- AT-02 → Extension 4a (reviewer already has five papers)  
- AT-03 → Extension 3a (workload retrieval failure)

Therefore, all test cases are valid, necessary, and should be kept, as each one verifies an explicit functional requirement of the use case.