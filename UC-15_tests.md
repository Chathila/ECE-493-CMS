## Acceptance Test Suite for UC-15: Generate Conference Schedule

### AT-01: Generate Schedule Successfully
**Preconditions**
- Administrator is registered and logged in.
- Accepted papers exist in the system.
- Scheduling data (rooms, time slots) is available.

**Steps**
1. Administrator selects **Generate Schedule**.
2. System generates the schedule.

**Expected Results**
- Schedule is generated successfully.
- Schedule is stored in the CMS database.
- Generated schedule is displayed to the administrator.

---

### AT-02: Missing Scheduling Data (Rooms/Time Slots)
**Preconditions**
- Administrator is logged in.
- Scheduling data is incomplete or missing.

**Steps**
1. Administrator selects **Generate Schedule**.

**Expected Results**
- System displays an error indicating missing scheduling information.
- Schedule is not generated or stored.

---

### AT-03: Scheduling Algorithm Fails to Produce Valid Schedule
**Preconditions**
- Administrator is logged in.
- Accepted papers and scheduling data exist.
- Simulate scheduling algorithm failure.

**Steps**
1. Administrator selects **Generate Schedule**.

**Expected Results**
- System displays an error indicating schedule generation failed.
- System logs the failure.
- Schedule is not stored in the database.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and both documented **extension flows**: missing/incomplete scheduling data (AT-02) and scheduling algorithm failure (AT-03). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-15.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, the UC-15 use case analysis suggested an additional operational flow: “schedule generation succeeds but saving to the database fails.” If that flow is added to the use case, include:

- **AT-04: Database Error While Saving Generated Schedule**
  - **Preconditions:** Administrator is logged in; accepted papers and scheduling data exist; simulate database failure on save.
  - **Steps:** Administrator selects **Generate Schedule**.
  - **Expected Results:** System shows an error indicating the schedule could not be saved; schedule is not stored; schedule may or may not be displayed depending on design, but it is not marked/persisted as generated.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow in UC-15 (main success or a documented extension). There are no orphan tests, so all current test cases are valid to keep.