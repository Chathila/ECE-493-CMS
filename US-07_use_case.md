# UC-07: Assign Referees to Submitted Papers

## Goal in Context
Allow an editor to assign referees to submitted papers so that each paper is properly reviewed according to conference rules.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Editor

## Secondary Actors
- Referee (Reviewer)
- CMS Notification Service
- CMS Database

## Trigger
Editor selects the option to assign referees for a submitted paper.

## Success End Condition
The selected referees are successfully assigned to the paper, the assignments are stored in the CMS database, and review invitations are sent to the referees.

## Failed End Condition
Referees are not assigned to the paper, and the system displays an appropriate error message explaining the reason for failure.

## Preconditions
- The editor is registered and logged in to the CMS.
- The paper has been successfully submitted.
- A list of available referees exists in the system.

## Main Success Scenario
1. Editor selects a submitted paper requiring referee assignment.
2. System displays the referee assignment interface.
3. Editor selects referees using their email addresses.
4. Editor submits the referee assignment request.
5. System verifies that the selected referees are eligible and have not exceeded the maximum allowed number of assigned papers.
6. System assigns the referees to the paper.
7. System stores the referee assignments in the CMS database.
8. System sends review invitation notifications to the assigned referees.

## Extensions
- **3a**: Selected referee email address does not exist in the system  
  - **3a1**: System displays an error message indicating an invalid referee email address.

- **5a**: Selected referee has exceeded the maximum allowed number of assigned papers  
  - **5a1**: System displays a workload violation message and prevents the assignment.

- **6a**: More than the allowed number of referees are assigned to the paper  
  - **6a1**: System displays an error message indicating the referee limit per paper.

## Related Information
- **Priority**: High  
- **Frequency**: Medium  
- **Open Issues**: Referee selection criteria based on research area may be enhanced in future CMS versions.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario and each extension (invalid referee email, workload limit exceeded, too many referees assigned) branches from appropriate steps in the assignment/validation process, and the behavior is plausible for enforcing conference assignment rules.

* Flow coverage: The scenarios cover the main success flow and key constraint violations (invalid referee identity, referee workload cap, and per-paper referee limit). However, the use case does not include a scenario where the editor assigns fewer than the required number of referees (e.g., fewer than three), which is implied by conference rules. Adding an extension for “insufficient referees selected” would improve completeness.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. Notification sending is included in the success condition and main flow, and no additional undocumented functionality is introduced.