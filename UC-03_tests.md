## Acceptance Test Suite for UC-03: Change User Password

### AT-01: Successful Password Change
**Preconditions**
- User is logged in.
- User account exists in CMS.

**Steps**
1. User opens account settings.
2. User selects **Change Password**.
3. User enters correct current password.
4. User enters a valid new password and confirms it.
5. User submits the request.

**Expected Results**
- Password is updated in the CMS database.
- Confirmation message is displayed.

---

### AT-02: Incorrect Current Password
**Preconditions**
- User is logged in.
- User account exists in CMS.

**Steps**
1. User opens **Change Password** form.
2. User enters an incorrect current password.
3. User enters a valid new password and confirms it.
4. User submits the request.

**Expected Results**
- Error message indicates current password is invalid.
- Password is not updated.

---

### AT-03: New Password Does Not Meet Security Requirements
**Preconditions**
- User is logged in.
- User account exists in CMS.

**Steps**
1. User opens **Change Password** form.
2. User enters correct current password.
3. User enters a weak/invalid new password.
4. User submits the request.

**Expected Results**
- Password guideline/error message is displayed.
- Password is not updated.

---

### AT-04: New Password and Confirmation Do Not Match
**Preconditions**
- User is logged in.
- User account exists in CMS.

**Steps**
1. User opens **Change Password** form.
2. User enters correct current password.
3. User enters a new password and a different confirmation password.
4. User submits the request.

**Expected Results**
- Error message indicates passwords must match.
- Password is not updated.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and all documented **extension flows**: incorrect current password (AT-02), new password fails security requirements (AT-03), and mismatched confirmation (AT-04). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-03.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. If you want stronger robustness coverage beyond the documented extensions, you could add an operational test for a database failure during the update step, but this is not required for strict flow coverage.

- **Optional AT-05: Database Error While Updating Password**
  - **Preconditions:** User is logged in; database/service failure is simulated.
  - **Steps:** Perform a valid password change submission while the database update fails.
  - **Expected Results:** Password is not updated; system displays an error message requesting retry.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow (main success or a documented extension). There are no tests that fail to cover a defined flow, so all current tests are valid to keep.