## Acceptance Test Suite for UC-19: Pay Conference Registration Fee

### AT-01: Successful Online Payment
**Preconditions**
- Attendee is registered and logged in.
- Registration prices are available.
- Attendee has selected a registration type.

**Steps**
1. Attendee opens the registration payment page.
2. Attendee enters valid payment information.
3. Attendee submits the payment.

**Expected Results**
- Payment is approved by the online payment service.
- Payment confirmation is recorded in the CMS database.
- Confirmation message is displayed to the attendee.

---

### AT-02: Invalid or Incomplete Payment Information
**Preconditions**
- Attendee is logged in and on the payment interface.

**Steps**
1. Attendee enters incomplete/invalid payment details.
2. Attendee submits the payment.

**Expected Results**
- System displays an error requesting corrected payment details.
- Payment is not processed and no confirmation is stored.

---

### AT-03: Payment Declined by Payment Service
**Preconditions**
- Attendee is logged in and on the payment interface.

**Steps**
1. Attendee enters valid payment information.
2. Payment service declines the transaction.

**Expected Results**
- System displays a payment failure message.
- Attendee is allowed to retry payment.
- No payment confirmation is recorded in CMS.

---

### AT-04: Payment Service Unavailable
**Preconditions**
- Attendee is logged in and on the payment interface.
- Simulate payment service downtime/unavailability.

**Steps**
1. Attendee submits payment with valid details.
2. System attempts to contact payment service.

**Expected Results**
- System displays an error indicating the payment service is temporarily unavailable.
- Payment is not completed and no confirmation is recorded.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and all documented **extension flows**: invalid/incomplete payment details (AT-02), payment declined (AT-03), and payment service unavailable (AT-04). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-19.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required for the documented UC-19 flows. However, UC-19’s success end condition includes “payment is recorded in the CMS database,” and failures could occur after the payment provider approves but before CMS records the result. If you want stronger flow coverage for real systems, add:

- **AT-05: Payment Approved but CMS Fails to Record Confirmation (Database Error)**
  - **Preconditions:** Attendee is logged in; simulate database failure on recording confirmation; payment service returns “approved.”
  - **Steps:** Submit valid payment information; payment service approves; CMS attempts to save confirmation.
  - **Expected Results:** System shows an error (or “payment received, but confirmation pending”); confirmation is not recorded; attendee is instructed on next steps (retry/refresh/support).

(Only include AT-05 if you decide this is part of the UC-19 flow set; it is not explicitly documented in the current UC-19 extensions.)

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow in UC-19 (main success or a documented extension). There are no orphan tests, so all current test cases are valid to keep.