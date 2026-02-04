## Acceptance Test Suite for UC-06: Validate Paper File Format and Size

### AT-01: Accept Valid File Format and Size
**Preconditions**
- Author is registered and logged in.
- Author is in the paper submission process.

**Steps**
1. Author uploads a manuscript file in an accepted format.
2. File is within the allowed size limit.

**Expected Results**
- System confirms file format is valid.
- System confirms file size is valid.
- Author can proceed to save or submit the paper.

---

### AT-02: Reject Unsupported File Format
**Preconditions**
- Author is logged in and in submission form.

**Steps**
1. Author uploads a manuscript file in an unsupported format.

**Expected Results**
- System displays an error listing accepted file formats.
- Upload is rejected and author cannot proceed with that file.

---

### AT-03: Reject File That Exceeds Size Limit
**Preconditions**
- Author is logged in and in submission form.

**Steps**
1. Author uploads a manuscript file in an accepted format that exceeds the size limit.

**Expected Results**
- System displays an error indicating the file size limit.
- Upload is rejected and author cannot proceed with that file.

---

### AT-04: Upload Failure (Network/System Error)
**Preconditions**
- Author is logged in and in submission form.
- Simulate network/system failure during upload.

**Steps**
1. Author attempts to upload a manuscript file.

**Expected Results**
- System displays an error indicating upload failed.
- Author is prompted to retry the upload.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and all documented **extension flows**: unsupported format (AT-02), file too large (AT-03), and upload failure due to system/network error (AT-04). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-06.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, since the use case analysis proposed an additional flow for corrupted/unreadable files, add the following test if that extension is included:

- **AT-05: Reject Corrupted/Unreadable File (if supported by requirements)**
  - **Preconditions:** Author is logged in and in submission form.
  - **Steps:** Upload a file with an accepted extension that is corrupted/unreadable (e.g., malformed PDF).
  - **Expected Results:** System rejects the file with an appropriate error message; author cannot proceed with that file.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow (main success or a documented extension). There are no tests that fail to cover a defined flow, so all current tests are valid to keep.