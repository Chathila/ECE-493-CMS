# UC-08: Enforce Reviewer Workload Limit

## Goal in Context
Prevent an editor from assigning more than five papers to a reviewer so that reviewer workloads remain fair.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Editor

## Secondary Actors
- CMS Validation Service  
- CMS Database

## Trigger
Editor attempts to assign a reviewer to a paper during referee assignment.

## Success End Condition
If the reviewer has fewer than five assigned papers, the system allows the assignment to proceed and records the updated workload.

## Failed End Condition
If the reviewer already has five assigned papers, the system blocks the assignment and informs the editor of the workload violation.

## Preconditions
- The editor is registered and logged in to the CMS.
- The paper is in a state where referees can be assigned.
- The reviewer exists in the system and has a recorded number of assigned papers.

## Main Success Scenario
1. Editor selects a submitted paper requiring referee assignment.
2. Editor selects a reviewer to assign to the paper.
3. System retrieves the reviewer’s current number of assigned papers.
4. System checks whether the reviewer has fewer than five assigned papers.
5. System allows the assignment to proceed.
6. System updates the reviewer’s assigned-paper count in the CMS database.
7. System confirms to the editor that the reviewer was successfully assigned.

## Extensions
- **4a**: Reviewer already has five assigned papers  
  - **4a1**: System blocks the assignment.
  - **4a2**: System displays a workload violation message to the editor.

- **3a**: System cannot retrieve reviewer workload information (database error)  
  - **3a1**: System displays an error message indicating the workload check could not be completed.
  - **3a2**: System prevents the assignment until the issue is resolved.

## Related Information
- **Priority**: High  
- **Frequency**: Medium  
- **Open Issues**: The workload limit value (5) may be configurable in future CMS releases.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
An editor who is logged in to the CMS selects a submitted paper that requires referee assignment and chooses a reviewer to assign to that paper. The system retrieves the reviewer’s current workload from the CMS database and verifies that the reviewer has fewer than five assigned papers. Since the workload limit has not been reached, the system allows the assignment to proceed, updates the reviewer’s assigned-paper count in the database, and confirms to the editor that the reviewer has been successfully assigned.

---

### Alternative Scenario Narrative (4a: Reviewer Already Has Five Assigned Papers)
The editor selects a reviewer for assignment to a submitted paper and submits the request. The system retrieves the reviewer’s current workload and determines that the reviewer already has five assigned papers, which is the maximum allowed. The system blocks the assignment, does not update the database, and displays a workload violation message informing the editor that the reviewer cannot be assigned additional papers.

---

### Alternative Scenario Narrative (3a: Reviewer Workload Retrieval Failure)
The editor attempts to assign a reviewer to a paper, but when the system tries to retrieve the reviewer’s current workload from the CMS database, an error occurs. Because the system cannot reliably determine whether the workload limit would be exceeded, it prevents the assignment from proceeding. The system displays an error message indicating that the workload check could not be completed and advises the editor to retry once the issue is resolved.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario and each extension (workload cap reached, workload retrieval failure) branches from appropriate steps in the workload-check process, and the behavior is plausible for enforcing assignment constraints.

* Flow coverage: The scenarios cover the main success flow and the two key failure conditions: reviewer already at the workload limit and inability to retrieve workload data. The set of scenarios provides sufficient coverage for the use case as specified.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. The database-error scenario is a reasonable operational alternative flow consistent with the goal and does not introduce new system functionality beyond error handling.