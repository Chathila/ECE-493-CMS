## Acceptance Test Suite for UC-18: View Conference Registration Prices

### AT-01: View Registration Prices Successfully
**Preconditions**
- Conference registration pricing information exists.
- CMS website is accessible.

**Steps**
1. User navigates to the registration or information section.
2. User selects **View Registration Prices**.

**Expected Results**
- System retrieves pricing information from the CMS database.
- Registration prices are displayed by attendee category.

---

### AT-02: Registration Pricing Information Not Available
**Preconditions**
- Pricing information is not available in the CMS.

**Steps**
1. User selects **View Registration Prices**.

**Expected Results**
- System displays a message indicating pricing information is currently unavailable.
- No pricing details are shown.

---

### AT-03: Database Error During Price Retrieval
**Preconditions**
- Pricing information exists.
- Simulate database retrieval failure.

**Steps**
1. User selects **View Registration Prices**.

**Expected Results**
- System displays an error message requesting the user to try again later.
- Registration prices are not displayed.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and both documented **extension flows**: pricing information not available (AT-02) and database retrieval failure (AT-03). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-18.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, if future requirements introduce pricing rules (e.g., discounts or user-specific pricing), additional tests would be needed to cover those new flows.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow in UC-18 (main success or a documented extension). There are no orphan tests, so all current test cases are valid to keep.