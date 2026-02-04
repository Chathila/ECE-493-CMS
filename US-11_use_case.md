# UC-11: Access Paper Review Form

## Goal in Context
Allow a referee to access the review form for papers assigned to them so that they can evaluate the papers.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Referee (Reviewer)

## Secondary Actors
- CMS Authorization Service  
- CMS Database

## Trigger
Referee selects an assigned paper to review from their CMS dashboard.

## Success End Condition
The review form for the selected paper is successfully displayed, allowing the referee to evaluate the paper.

## Failed End Condition
The review form is not displayed, and the system shows an appropriate error message.

## Preconditions
- The referee is registered and logged in to the CMS.
- The referee has accepted the review invitation for the paper.
- The paper is assigned to the referee.

## Main Success Scenario
1. Referee logs in to the CMS.
2. Referee navigates to their list of assigned papers.
3. Referee selects a paper to review.
4. System verifies that the referee is authorized to review the selected paper.
5. System retrieves the review form and paper details.
6. System displays the review form to the referee.

## Extensions
- **4a**: Referee is not authorized to access the selected paper  
  - **4a1**: System displays an access-denied error message.

- **5a**: Review form cannot be retrieved due to a system or database error  
  - **5a1**: System displays an error message indicating the form is unavailable.

## Related Information
- **Priority**: High  
- **Frequency**: High  
- **Open Issues**: Review form content and structure may be updated in future CMS versions.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
A referee logs in to the CMS and navigates to their dashboard, where a list of assigned papers is displayed. The referee selects one of the papers they have accepted for review. The system verifies that the referee is authorized to access the selected paper and retrieves the associated review form along with the paper details from the CMS database. The review form is then displayed, allowing the referee to begin evaluating the paper.

---

### Alternative Scenario Narrative (4a: Unauthorized Access)
A referee attempts to access the review form for a paper that is not assigned to them or for which they have not accepted the review invitation. The system checks the referee’s authorization and determines that access is not permitted. The system displays an access-denied error message, and the review form is not shown.

---

### Alternative Scenario Narrative (5a: Review Form Retrieval Failure)
A referee selects an assigned paper to review, and the system confirms that the referee is authorized. While attempting to retrieve the review form and paper details, the system encounters a system or database error. The system displays an error message indicating that the review form is currently unavailable, and the referee is unable to proceed until the issue is resolved.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario follows a plausible sequence for accessing an assigned review form, and the extensions (authorization failure, retrieval failure) branch from appropriate system checks.

* Flow coverage: The scenarios cover the main success flow and two primary failure cases: unauthorized access and system/database retrieval failure. However, the use case does not include a scenario where the referee has not accepted the invitation (precondition violation) but attempts to access the form anyway. Adding an extension for “invitation not accepted / assignment not active” would improve completeness and clarify enforcement of the precondition.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. All scenarios align with the goal, stated preconditions, and authorization/retrieval steps, and no undocumented functionality is introduced.