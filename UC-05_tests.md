## Acceptance Test Suite for UC-05: Save Paper Submission Draft

### AT-01: Successfully Save Draft
**Preconditions**
- Author is registered and logged in.
- Author has started a paper submission.

**Steps**
1. Author enters or updates paper information in the submission form.
2. Author clicks **Save**.

**Expected Results**
- System validates the entered information.
- Draft is saved in the CMS database.
- Confirmation message indicates the draft was saved.

---

### AT-02: Save Draft with Invalid or Incomplete Data
**Preconditions**
- Author is logged in.
- Author is in the submission form.

**Steps**
1. Author enters invalid or incomplete information (e.g., missing required draft fields or invalid characters).
2. Author clicks **Save**.

**Expected Results**
- Error message indicates invalid or incomplete information.
- Draft is not saved.

---

### AT-03: Database/Storage Error During Save
**Preconditions**
- Author is logged in.
- Simulate a database/storage failure.

**Steps**
1. Author enters valid paper information.
2. Author clicks **Save**.

**Expected Results**
- Error message indicates the draft could not be saved.
- Draft is not saved in the CMS database.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and both documented **extension flows**: validation failure (AT-02) and database/storage error (AT-03). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-05.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, because the use case notes “partial validation rules may be refined,” add tests if those rules are clarified/added to the use case:

- **AT-04: Save Draft With Allowed Partial Data (if partial drafts are permitted)**
  - **Preconditions:** Author is logged in; partial draft rules are defined.
  - **Steps:** Leave non-mandatory fields blank; click **Save**.
  - **Expected Results:** Draft saves successfully; confirmation is shown.

- **AT-05: Save Draft With No Changes (no-op save)**
  - **Preconditions:** Draft already exists.
  - **Steps:** Open existing draft; make no edits; click **Save**.
  - **Expected Results:** System confirms save (or indicates no changes) and draft remains unchanged.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow (main success or a documented extension). There are no tests that fail to cover a defined flow, so all current tests are valid to keep.