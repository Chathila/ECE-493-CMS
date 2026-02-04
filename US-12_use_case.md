# UC-12: Submit Paper Review

## Goal in Context
Allow a referee to submit a completed review so that the editor can make a decision on the paper.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Referee (Reviewer)

## Secondary Actors
- Editor  
- CMS Validation Service  
- CMS Database

## Trigger
Referee submits the completed review form for an assigned paper.

## Success End Condition
The completed review is successfully stored in the CMS database and made available to the editor.

## Failed End Condition
The review is not submitted, and the system displays an appropriate error message explaining the reason for failure.

## Preconditions
- The referee is registered and logged in to the CMS.
- The referee has accepted the review invitation.
- The referee has accessed and completed the review form.

## Main Success Scenario
1. Referee accesses the review form for an assigned paper.
2. Referee completes all required review fields.
3. Referee submits the completed review form.
4. System validates the review form for completeness and correctness.
5. System stores the review in the CMS database.
6. System notifies the editor that a review has been submitted.

## Extensions
- **4a**: One or more required review fields are incomplete or invalid  
  - **4a1**: System displays an error message requesting completion or correction.

- **5a**: System encounters a database or storage error  
  - **5a1**: System displays an error message and requests the referee to retry submission.

## Related Information
- **Priority**: High  
- **Frequency**: Medium  
- **Open Issues**: Review submission deadlines and reminder notifications may be added in future CMS versions.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
A referee who has accepted a review invitation logs in to the CMS and opens the review form for an assigned paper. After carefully evaluating the paper, the referee completes all required review fields, such as scores, comments, and recommendations. The referee submits the completed review form. The system validates the submission to ensure all required information is present and correctly formatted, then stores the review in the CMS database. Once saved, the system notifies the editor that a new review has been submitted and is ready for consideration in the final decision process.

---

### Alternative Scenario Narrative (4a: Incomplete or Invalid Review Fields)
A referee attempts to submit a review without completing all required fields or enters invalid information (such as missing scores or improperly formatted input). During validation, the system detects the issue and prevents submission. An error message is displayed, clearly indicating which fields require completion or correction. The review is not stored, and the referee is prompted to revise the submission before attempting again.

---

### Alternative Scenario Narrative (5a: Database or Storage Error)
A referee submits a fully completed and valid review form. The system successfully validates the review content, but encounters a database or storage error while attempting to save the review. The system displays an error message informing the referee that the submission could not be completed at this time and requests that they retry later. The review is not stored, and the editor is not notified until a successful submission occurs.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario reflects a realistic review-submission interaction, and the extensions (invalid/incomplete fields, database/storage failure) branch from appropriate validation and persistence steps.

* Flow coverage: The scenarios cover the main success flow and the primary failure cases related to form validation and storage. However, the use case does not include a scenario where the referee attempts to submit after a submission deadline (noted as a potential future requirement). If deadlines are introduced, an extension for “submission window expired” should be added.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. Editor notification is consistent with the success end condition and does not introduce additional undocumented functionality.