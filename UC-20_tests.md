## Acceptance Test Suite for UC-20: Receive Registration Payment Confirmation Ticket

### AT-01: Ticket Generated, Stored, and Delivered Successfully
**Preconditions**
- Attendee is registered and logged in.
- Registration payment is completed successfully.
- Attendee has valid contact information.

**Steps**
1. System receives payment confirmation.
2. System generates the confirmation ticket.
3. System stores the ticket in the CMS.
4. System sends the ticket to the attendee.

**Expected Results**
- Ticket is stored in the CMS database.
- Attendee receives the confirmation ticket (email/system notification).

---

### AT-02: Ticket Delivery Failure
**Preconditions**
- Payment is completed successfully.
- Simulate email/system notification failure.

**Steps**
1. System generates and stores the ticket.
2. System attempts to deliver the ticket to the attendee.

**Expected Results**
- System logs the delivery failure.
- System informs the attendee of the delivery issue.
- Ticket remains available in the CMS (if stored successfully).

---

### AT-03: Attendee Accesses Ticket Before Delivery
**Preconditions**
- Payment is completed successfully.
- Ticket is generated and stored, but not yet delivered.

**Steps**
1. Attendee navigates to their registration/ticket area in the CMS.
2. Attendee attempts to view/download the ticket.

**Expected Results**
- System allows the attendee to view or download the ticket from the CMS.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and both documented **extension flows**: ticket delivery failure (AT-02) and attendee accessing the ticket before delivery (AT-03). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-20.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, UC-20 has a precondition that the attendee has valid contact information. If you want explicit coverage of that failure condition (often treated as an extension), add:

- **AT-04: Missing/Invalid Attendee Contact Information**
  - **Preconditions:** Payment completed successfully; attendee contact info is missing/invalid.
  - **Steps:** System attempts to deliver the ticket.
  - **Expected Results:** System logs the contact issue; attendee is informed that delivery failed due to contact info; ticket remains accessible in the CMS if stored.

Also, if you want stronger robustness coverage for real systems (not explicitly documented), consider:
- **AT-05: Failure to Store Ticket (Database Error)** — ticket generation succeeds but cannot be saved; system reports error and attendee cannot retrieve it.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow in UC-20 (main success or a documented extension). There are no orphan tests, so all current test cases are valid to keep. AT-03 is especially valuable because it verifies the requirement that the ticket is accessible in the CMS independent of delivery timing.