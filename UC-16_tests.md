## Acceptance Test Suite for UC-16: Edit Conference Schedule

### AT-01: Edit and Save Schedule Successfully
**Preconditions**
- Editor is registered and logged in.
- A generated schedule exists and is editable.

**Steps**
1. Editor opens the generated schedule in edit mode.
2. Editor makes valid changes (e.g., update session time/room).
3. Editor submits the updated schedule.

**Expected Results**
- System validates schedule (no conflicts, complete).
- Updated schedule is saved in the CMS database.
- Confirmation message indicates schedule was updated.

---

### AT-02: Scheduling Conflict Introduced (Overlap)
**Preconditions**
- Editor is logged in.
- Editable schedule exists.

**Steps**
1. Editor edits the schedule to create a conflict (e.g., same room/time overlap).
2. Editor submits the updated schedule.

**Expected Results**
- System highlights the conflict.
- System requests correction.
- Schedule is not saved.

---

### AT-03: Invalid or Incomplete Schedule Edit
**Preconditions**
- Editor is logged in.
- Editable schedule exists.

**Steps**
1. Editor makes an invalid/incomplete edit (e.g., missing room or invalid time).
2. Editor submits the updated schedule.

**Expected Results**
- System displays an error indicating invalid/incomplete scheduling info.
- Schedule is not saved.

---

### AT-04: Database Error While Saving Schedule
**Preconditions**
- Editor is logged in.
- Editable schedule exists.
- Simulate database error during save.

**Steps**
1. Editor makes valid edits.
2. Editor submits the updated schedule.

**Expected Results**
- System displays an error and requests retry.
- Schedule changes are not saved in the database.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and all documented **extension flows**: conflict introduced (AT-02), validation failure due to invalid/incomplete edits (AT-03), and database error while saving (AT-04). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-16.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, UC-16’s open issues mention versioning/rollback. If that becomes a documented flow later, you would add tests such as:
- “Rollback to previous schedule version”
- “View schedule version history”
These are not required for the current UC-16 scope.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow in UC-16 (main success or a documented extension). There are no orphan tests, so all current test cases are valid to keep. AT-02 and AT-03 are both worth keeping even though they are both “validation failures,” because they represent different failure causes (conflict vs malformed/incomplete data).