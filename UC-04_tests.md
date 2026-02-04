## Acceptance Test Suite for UC-04: Submit Paper Manuscript

### AT-01: Successful Paper Submission
**Preconditions**
- Author is registered and logged in.
- Submission period is open.

**Steps**
1. Author opens **Submit Paper** from dashboard.
2. Author fills all required paper metadata.
3. Author uploads a supported manuscript file within size limit.
4. Author submits the form.

**Expected Results**
- System validates metadata and file rules.
- Paper metadata and file are stored in the CMS database/storage.
- Success message is displayed.
- Author is redirected to the dashboard.

---

### AT-02: Missing Required Metadata
**Preconditions**
- Author is logged in.
- Submission period is open.

**Steps**
1. Author opens the submission form.
2. Author leaves one or more required fields empty.
3. Author uploads a valid file.
4. Author submits the form.

**Expected Results**
- Error message indicates missing required information.
- Paper is not submitted/stored.

---

### AT-03: Unsupported File Format
**Preconditions**
- Author is logged in.
- Submission period is open.

**Steps**
1. Author fills all required metadata.
2. Author uploads a file in an unsupported format.
3. Author submits the form.

**Expected Results**
- Error message indicates allowed file formats.
- Paper is not submitted/stored.

---

### AT-04: File Size Exceeds Limit
**Preconditions**
- Author is logged in.
- Submission period is open.

**Steps**
1. Author fills all required metadata.
2. Author uploads a supported format file that exceeds the size limit.
3. Author submits the form.

**Expected Results**
- Error message indicates the file size limit.
- Paper is not submitted/stored.

---

### AT-05: Invalid Form Data
**Preconditions**
- Author is logged in.
- Submission period is open.

**Steps**
1. Author enters invalid metadata (e.g., invalid characters or invalid email in contact info).
2. Author uploads a valid file.
3. Author submits the form.

**Expected Results**
- System highlights invalid fields and requests correction.
- Paper is not submitted/stored.

## Acceptance test suite analysis (UC-04)

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and all documented **extension flows**: missing required metadata (AT-02), unsupported file format (AT-03), file too large (AT-04), and invalid form data (AT-05). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-04.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, because the use case analysis proposes an additional operational extension (upload/storage failure), add the following test if that flow is included in the use case:

- **AT-06: File Upload or Storage Failure**
  - **Preconditions:** Author is logged in; submission period open; upload/storage failure is simulated.
  - **Steps:** Fill valid metadata; attempt to upload/submit a valid manuscript while the file service/network fails.
  - **Expected Results:** System displays an upload/storage error; paper is not stored/submitted; user is prompted to retry.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow (main success or a documented extension). There are no tests that fail to cover a defined flow, so all current tests are valid to keep.