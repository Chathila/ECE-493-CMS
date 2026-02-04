## Acceptance Test Suite for UC-02: User Login

### AT-01: Successful Login with Valid Credentials
**Preconditions**
- User account exists in CMS.
- Login page is available.

**Steps**
1. User opens the login page.
2. User enters a registered email and correct password.
3. User submits the login form.

**Expected Results**
- User is authenticated successfully.
- User is redirected to their authorized home page.

---

### AT-02: Missing Required Fields
**Preconditions**
- Login page is available.

**Steps**
1. User opens the login page.
2. User leaves email and/or password empty.
3. User submits the login form.

**Expected Results**
- Error message indicating missing required fields is displayed.
- User is not authenticated.

---

### AT-03: Incorrect Email or Password
**Preconditions**
- User account exists in CMS.
- Login page is available.

**Steps**
1. User enters a registered email with an incorrect password (or incorrect email with any password).
2. User submits the login form.

**Expected Results**
- Authentication failure message is displayed.
- User is not authenticated and remains on the login page.

---

### AT-04: Inactive or Locked Account
**Preconditions**
- User account exists but is marked inactive/locked in CMS.
- Login page is available.

**Steps**
1. User enters the inactive/locked account credentials.
2. User submits the login form.

**Expected Results**
- Account status message is displayed (inactive/locked).
- User is denied access and not redirected to the home page.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and all documented **extension flows**: missing required fields (AT-02), incorrect credentials (AT-03), and inactive/locked account (AT-04). Therefore, the suite achieves **full flow coverage** for the flows currently documented in UC-02.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, if a lockout policy is added (as noted in the open issues), add the following test to maintain all-flows coverage:

- **AT-05: Account Lockout After Repeated Failed Login Attempts**
  - **Preconditions:** User account exists; lockout threshold is defined (e.g., N failed attempts).
  - **Steps:** Attempt login with incorrect credentials repeatedly until the threshold is reached; then attempt login again (even with correct credentials, depending on policy).
  - **Expected Results:** Account is locked (or temporarily blocked) per policy; system displays lockout/timeout message; access is denied.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow (main success or a documented extension). There are no tests that fail to cover a defined flow, so all current tests are valid to keep.