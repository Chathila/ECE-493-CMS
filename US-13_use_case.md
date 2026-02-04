# UC-13: Make Final Paper Decision

## Goal in Context
Allow an editor to make an acceptance or rejection decision for a paper once all assigned reviews are completed.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Editor

## Secondary Actors
- Author  
- CMS Validation Service  
- CMS Database  
- CMS Notification Service

## Trigger
All required reviews for a paper have been submitted.

## Success End Condition
The editor’s decision is successfully recorded in the CMS database, and the author is notified of the acceptance or rejection.

## Failed End Condition
The decision is not recorded, and the system displays an appropriate error message.

## Preconditions
- The editor is registered and logged in to the CMS.
- The paper has received all required review submissions.
- The review period is closed for the paper.

## Main Success Scenario
1. Editor logs in to the CMS.
2. Editor navigates to the list of papers awaiting final decision.
3. Editor selects a paper with all completed reviews.
4. System displays the submitted reviews and evaluation details.
5. Editor selects either **Accept** or **Reject**.
6. Editor submits the final decision.
7. System validates that all required reviews are present.
8. System records the decision in the CMS database.
9. System notifies the author of the decision.

## Extensions
- **3a**: Editor attempts to make a decision before all reviews are completed  
  - **3a1**: System displays an error message indicating that reviews are incomplete.

- **8a**: System fails to record the decision due to a database error  
  - **8a1**: System displays an error message and requests the editor to retry.

## Related Information
- **Priority**: High  
- **Frequency**: Medium  
- **Open Issues**: Decision criteria and decision deadlines may be configurable in future CMS versions.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
An editor logs in to the CMS and navigates to the list of papers awaiting final decisions. The editor selects a paper for which all assigned reviews have been completed and the review period has closed. The system displays the collected reviews and any supporting evaluation details. After considering the reviewers’ feedback, the editor selects either **Accept** or **Reject** and submits the decision. The system verifies that all required reviews are present, records the final decision in the CMS database, and triggers a notification to inform the author of the outcome.

---

### Alternative Scenario Narrative (3a: Reviews Incomplete)
An editor attempts to make a final decision on a paper that has not yet received all required reviews. When the editor selects the paper or attempts to submit a decision, the system detects that one or more reviews are missing. The system blocks the action and displays an error message indicating that the reviews are incomplete. No decision is recorded, and the editor must wait until all required reviews have been submitted before proceeding.

---

### Alternative Scenario Narrative (8a: Database Error While Recording Decision)
An editor selects **Accept** or **Reject** for a paper that has all required reviews and submits the final decision. Although the decision itself is valid, the system encounters a database error while attempting to store the decision. The system displays an error message informing the editor that the decision could not be recorded and requests that they retry the action. The decision is not saved, and the author is not notified until the decision is successfully recorded.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario follows a plausible editor decision process, and the extensions (reviews incomplete, database failure) branch from appropriate decision and persistence steps.

* Flow coverage: The scenarios cover the main success flow and key failure cases: attempting a decision before reviews are complete and failure to record the decision. However, the use case does not include a scenario where the author notification fails (e.g., notification service error). Adding an extension for “decision recorded but notification fails” would improve completeness and align with the stated success end condition.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. All scenarios align with the goal, preconditions, and validation/recording steps, and no undocumented functionality is introduced.