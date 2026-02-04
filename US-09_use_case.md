# UC-09: Receive Review Invitation Email

## Goal in Context
Notify a referee via email when they are assigned to review a paper so that they are aware of the review request.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Referee (Reviewer)

## Secondary Actors
- Editor  
- CMS Notification Service  
- Email Service

## Trigger
Editor assigns the referee to a paper.

## Success End Condition
The referee receives an email invitation containing the paper information and instructions to accept or reject the review request.

## Failed End Condition
The referee does not receive the invitation email, and the system records the failure for follow-up.

## Preconditions
- The referee is registered in the CMS with a valid email address.
- The editor has successfully assigned the referee to a paper.

## Main Success Scenario
1. Editor assigns a referee to a submitted paper.
2. System generates a review invitation message containing paper details.
3. System sends the invitation email to the referee’s registered email address.
4. Referee receives the review invitation email.

## Extensions
- **3a**: Email service is unavailable or fails to send the invitation  
  - **3a1**: System logs the email delivery failure.
  - **3a2**: System notifies the editor of the delivery issue.

- **4a**: Referee’s email address is invalid  
  - **4a1**: System records the error and alerts the editor to update referee contact information.

## Related Information
- **Priority**: High  
- **Frequency**: Medium  
- **Open Issues**: Email retry and notification policies may be refined in future CMS versions.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
An editor assigns a referee to a submitted paper in the CMS. Once the assignment is confirmed, the system automatically generates a review invitation email containing the paper title, abstract, and clear instructions for accepting or rejecting the review request. The system sends this email to the referee’s registered email address via the email service. The referee successfully receives the invitation email and becomes aware of the review request.

---

### Alternative Scenario Narrative (3a: Email Service Unavailable or Delivery Failure)
After the editor assigns a referee to a paper, the system generates the review invitation email and attempts to send it. The email service is unavailable or fails during delivery, preventing the message from being sent. The system records the delivery failure in its logs and notifies the editor that the invitation email could not be delivered so that corrective action (such as retrying or updating contact details) can be taken.

---

### Alternative Scenario Narrative (4a: Invalid Referee Email Address)
The editor assigns a referee to a submitted paper, and the system attempts to send the review invitation email. During the sending process, the system detects that the referee’s email address is invalid. The system records the error and alerts the editor that the referee’s contact information needs to be updated. The invitation email is not delivered, and the referee is not notified until the issue is resolved.

## Use case analysis
* Scenario validity and plausibility: The scenarios are plausible for an email-notification workflow, but the “main success scenario” is only weakly instantiated as a user-goal use case because the referee performs no actions and the flow is driven by the editor/system. It reads more like a system-triggered notification event than an interactive referee use case.

* Flow coverage: The scenarios cover successful delivery and the main failure conditions (email service failure and invalid email address). However, the use case does not include a scenario for delayed delivery/retry behavior (e.g., the system queues and retries sending). If retry is intended, an extension should be added once the retry policy is defined.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. That said, this use case overlaps with UC-07 (Assign Referees), where sending invitations is already part of the success path; it may be more appropriate to treat this as a sub-flow/step within UC-07 rather than a standalone use case, unless the course expects notifications to be modeled separately.