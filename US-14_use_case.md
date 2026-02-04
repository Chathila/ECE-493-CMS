# UC-14: Receive Final Paper Decision

## Goal in Context
Notify an author of the final acceptance or rejection decision for their submitted paper so that they know the paper’s status.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Author

## Secondary Actors
- Editor  
- CMS Notification Service  
- CMS Database  
- Email Service

## Trigger
Editor submits the final decision for a paper.

## Success End Condition
The author receives the final decision notification, and the decision status is accessible in the CMS.

## Failed End Condition
The author does not receive the decision notification, and the system records the failure for follow-up.

## Preconditions
- The author is registered in the CMS.
- The paper has a finalized decision recorded by the editor.
- The author has valid contact information in the system.

## Main Success Scenario
1. Editor submits the final decision for a paper.
2. System records the decision in the CMS database.
3. System generates a decision notification message for the author.
4. System sends the decision notification to the author.
5. Author receives the notification.
6. Author views the decision status in the CMS.

## Extensions
- **4a**: Notification delivery fails (email or system notification error)  
  - **4a1**: System logs the delivery failure.
  - **4a2**: System notifies the editor of the issue.

- **6a**: Author attempts to view the decision before notification delivery  
  - **6a1**: System displays the decision status directly from the CMS database.

## Related Information
- **Priority**: High  
- **Frequency**: Medium  
- **Open Issues**: Notification channels (email vs. in-system only) may be configurable in future CMS versions.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
After all reviews are completed, the editor submits a final **Accept** or **Reject** decision for the paper. The system records this decision in the CMS database and generates a notification message addressed to the author. The notification service successfully delivers the message through the configured channel (e.g., email or in-system notification). The author receives the notification and accesses the CMS to view the final decision status associated with their submitted paper.

---

### Alternative Scenario Narrative (4a: Notification Delivery Failure)
The editor submits the final decision, and the system successfully records it in the CMS database. When the system attempts to deliver the decision notification, the email or notification service fails. The system logs the delivery failure for auditing and troubleshooting purposes and notifies the editor that the author may not have received the decision. Although the notification was not delivered, the decision remains stored in the CMS and is available for the author to view directly within the system.

---

### Alternative Scenario Narrative (6a: Author Views Decision Before Notification Delivery)
The editor submits the final decision and the system records it in the CMS database. Before the decision notification is delivered (for example, due to a delivery delay or failure), the author logs in to the CMS and navigates to the paper status page. The system retrieves the final decision directly from the CMS database and displays it to the author, allowing them to view the outcome even without receiving the notification.

## Use case analysis
* Scenario validity and plausibility: The scenarios are plausible for a decision-notification workflow, but the main success scenario is only partially instantiated from the author’s perspective because steps 1–4 are driven by the editor/system. The author actions (receiving the notification and viewing the status) are valid, but the flow reads partly as a system event description rather than a purely author-initiated use case.

* Flow coverage: The scenarios cover successful notification delivery, notification failure, and the case where the author views the decision directly from the database even before receiving the notification. However, the use case does not include a scenario where the decision is recorded but the author’s contact information is missing/invalid (precondition violation). Adding an extension for “invalid/missing author contact info” would improve completeness.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. Similar to UC-13, this use case overlaps with the editor decision flow (where author notification is part of the success path); it may be more appropriate as a sub-flow/step under UC-13 unless the assignment expects notifications to be modeled as separate use cases.