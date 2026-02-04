## Acceptance Test Suite for UC-09: Receive Review Invitation Email

### AT-01: Invitation Email Sent Successfully
**Preconditions**
- Referee is registered with a valid email address.
- Editor assigns the referee to a paper.

**Steps**
1. Editor assigns the referee to a submitted paper.
2. System generates the invitation email.
3. System sends the email.

**Expected Results**
- Referee receives the invitation email containing paper details and accept/reject instructions.

---

### AT-02: Email Service Failure
**Preconditions**
- Referee is registered with a valid email address.
- Simulate email service being unavailable/failing.

**Steps**
1. Editor assigns the referee to a paper.
2. System attempts to send the invitation email.

**Expected Results**
- System logs the email delivery failure.
- System notifies the editor of the delivery issue.
- Referee does not receive the email.

---

### AT-03: Invalid Referee Email Address
**Preconditions**
- Referee exists in CMS but has an invalid email address on record.

**Steps**
1. Editor assigns the referee to a paper.
2. System attempts to send the invitation email.

**Expected Results**
- System records the error.
- System alerts the editor to update referee contact information.
- Referee does not receive the email.

## Acceptance test suite analysis

### Flow coverage check
The acceptance tests cover the **main success flow** (AT-01) and both documented **extension flows**: email service failure (AT-02) and invalid referee email address (AT-03). Therefore, the suite achieves **full flow coverage** for the flows documented in UC-09.

### (a) Missing-flow tests to achieve flow coverage
No additional tests are required to cover the documented flows. However, if UC-09 is expanded to include retry/queue behavior (noted as an open issue), add the following test to maintain flow coverage once the retry policy is defined:

- **AT-04: Email Retry/Deferred Delivery (if retry policy is introduced)**
  - **Preconditions:** Retry policy is defined; simulate temporary email failure that later recovers.
  - **Steps:** Trigger invitation send during email outage; restore service; observe retry behavior.
  - **Expected Results:** System retries per policy; invitation is eventually delivered; editor is informed appropriately.

### (b) Tests that do not map to a specific flow
All existing tests map directly to a specific flow in UC-09 (main success or a documented extension). There are no orphan tests, so all current test cases are valid to keep. Even though UC-09 overlaps with UC-07 (sending invitations), these tests remain valuable because they verify the notification delivery behavior and failure handling explicitly.