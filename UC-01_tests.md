## Acceptance Test Suite for UC-01: Register User Account

### AT-01: Successful User Registration
**Preconditions**
- User email is not registered.
- Registration page is available.

**Steps**
1. User opens the registration page.
2. User enters a valid email and password.
3. User submits the registration form.

**Expected Results**
- User account is created in the CMS database.
- Confirmation message is displayed.
- User is redirected to the login page.

---

### AT-02: Missing Required Fields
**Preconditions**
- Registration page is available.

**Steps**
1. User opens the registration page.
2. User leaves one or more required fields empty.
3. User submits the form.

**Expected Results**
- Error message indicating missing required fields is displayed.
- User account is not created.

---

### AT-03: Invalid Email Format
**Preconditions**
- Registration page is available.

**Steps**
1. User enters an invalid email format.
2. User enters a valid password.
3. User submits the form.

**Expected Results**
- Error message requesting a valid email address is displayed.
- User account is not created.

---

### AT-04: Duplicate Email Address
**Preconditions**
- Email address is already registered in the CMS.

**Steps**
1. User enters an existing email address.
2. User enters a valid password.
3. User submits the form.

**Expected Results**
- Error message indicating the email address is already registered.
- User account is not created.

---

### AT-05: Weak Password
**Preconditions**
- Registration page is available.

**Steps**
1. User enters a valid email address.
2. User enters a password that does not meet security requirements.
3. User submits the form.

**Expected Results**
- Error message displaying password requirements is shown.
- User account is not created.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and all documented **extension flows**: missing required fields (AT-02), invalid email (AT-03), duplicate email (AT-04), and weak password (AT-05). Therefore, the suite achieves **full flow coverage** for the flows currently documented in UC-01.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, if the proposed extension “user abandons registration prior to submission” is added to UC-01, add the following test to maintain all-flows coverage:

- **AT-06: User Abandons Registration Before Submitting**
  - **Preconditions:** Registration page is available.
  - **Steps:** User opens the registration form and exits/navigates away without submitting.
  - **Expected Results:** No account is created; no data is saved; user is returned to the prior page/homepage.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow (main success or a documented extension). There are no “orphan” tests that fail to cover a defined flow, so all current tests are valid to keep.