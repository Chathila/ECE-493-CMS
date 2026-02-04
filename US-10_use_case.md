# UC-10: Accept or Reject Review Invitation

## Goal in Context
Allow a referee to accept or reject a review invitation so that they can manage their review workload.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Referee (Reviewer)

## Secondary Actors
- CMS Notification Service  
- CMS Database

## Trigger
Referee selects the accept or reject option from a review invitation.

## Success End Condition
The referee’s response is successfully recorded, and the system updates the paper’s review assignment status accordingly.

## Failed End Condition
The referee’s response is not recorded, and the system displays an appropriate error message.

## Preconditions
- The referee is registered in the CMS.
- The referee has received a valid review invitation.
- The invitation is still open for response.

## Main Success Scenario
1. Referee opens the review invitation.
2. System displays paper details and available response options.
3. Referee selects either **Accept** or **Reject**.
4. Referee submits their response.
5. System records the referee’s response in the CMS database.
6. System updates the review assignment status.
7. System notifies the editor of the referee’s decision.

## Extensions
- **3a**: Referee attempts to respond after the invitation has expired  
  - **3a1**: System displays an error message indicating the invitation is no longer valid.

- **5a**: System fails to record the response due to a database error  
  - **5a1**: System displays an error message and requests the referee to retry.

## Related Information
- **Priority**: High  
- **Frequency**: Medium  
- **Open Issues**: Invitation response deadlines may be configurable in future CMS releases.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
A referee receives a review invitation and opens it through the CMS. The system displays the paper details along with clear options to **Accept** or **Reject** the review request. After reviewing the information, the referee selects an option and submits their response. The system successfully records the referee’s decision in the CMS database, updates the review assignment status for the paper, and notifies the editor of the referee’s response so that the review process can proceed accordingly.

---

### Alternative Scenario Narrative (3a: Invitation Expired)
A referee opens a review invitation and attempts to respond by selecting **Accept** or **Reject**. The system detects that the invitation has expired and is no longer open for responses. The system displays an error message informing the referee that the invitation is no longer valid, and no response is recorded or assignment status updated.

---

### Alternative Scenario Narrative (5a: Database Error While Recording Response)
A referee opens a valid review invitation, selects **Accept** or **Reject**, and submits their response. While attempting to record the response, the system encounters a database error. The system displays an error message indicating that the response could not be saved and prompts the referee to retry. The review assignment status remains unchanged, and the editor is not notified until the response is successfully recorded.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario reflects a realistic invitation-response interaction, and the extensions (expired invitation, database failure) branch from appropriate steps and are plausible within CMS operations.

* Flow coverage: The scenarios cover the main success flow and two important failure cases (expired invitation and failure to record the response). However, the use case does not include a scenario where the referee views the invitation but takes no action (abandons without submitting), which is a realistic user behavior. Adding an extension for “referee closes invitation without responding” would improve completeness.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. The editor notification step is consistent with the success end condition and does not introduce undocumented functionality beyond communicating the recorded decision.