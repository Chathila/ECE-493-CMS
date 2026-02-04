# UC-05: Save Paper Submission Draft

## Goal in Context
Allow an author to save a paper submission at any stage of the submission process so that progress is not lost.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Author

## Secondary Actors
- CMS Validation Service  
- CMS Database

## Trigger
Author selects the **Save** option during the paper submission process.

## Success End Condition
The current state of the paper submission is successfully validated and saved in the CMS database, and the author receives confirmation that the draft has been saved.

## Failed End Condition
The paper submission draft is not saved, and the system displays an appropriate error message explaining the reason for failure.

## Preconditions
- The author is registered and logged in to the CMS.
- The author has started a paper submission.

## Main Success Scenario
1. Author is in the paper submission form.
2. Author enters or updates paper information.
3. Author selects the **Save** option.
4. System validates the entered paper information.
5. System stores the current submission data as a draft in the CMS database.
6. System displays a confirmation message indicating the draft was saved successfully.

## Extensions
- **4a**: Validation of entered data fails  
  - **4a1**: System displays an error message indicating invalid or incomplete information.

- **5a**: System encounters a database or storage error  
  - **5a1**: System displays an error message indicating the draft could not be saved.

## Related Information
- **Priority**: High  
- **Frequency**: Medium  
- **Open Issues**: Partial validation rules for draft submissions may be refined in future CMS versions.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
An author who is logged in to the CMS is working within the paper submission form and has entered or updated some paper information. To avoid losing progress, the author selects the **Save** option. The system validates the currently entered information according to the draft submission rules and successfully stores the submission state as a draft in the CMS database. Once the draft is saved, the system displays a confirmation message indicating that the submission has been saved successfully, allowing the author to continue editing later.

---

### Alternative Scenario Narrative (4a: Validation of Entered Data Fails)
An author attempts to save a draft after entering paper information that does not satisfy the draft validation rules, such as missing required draft fields or invalid data formats. When the author selects **Save**, the system validates the input, detects the invalid or incomplete information, and displays an error message explaining the issue. The draft is not saved, and the author remains on the submission form to correct the information before retrying.

---

### Alternative Scenario Narrative (5a: Database or Storage Error)
An author selects the **Save** option after entering valid paper information, but the system encounters a database or storage error while attempting to persist the draft. The system fails to save the submission data and displays an error message indicating that the draft could not be saved due to a system issue. The author is informed of the failure and may attempt to save the draft again once the issue is resolved.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario and each extension branch from appropriate steps (validation and persistence), and the behavior is plausible for saving an in-progress submission.

* Flow coverage: The scenarios cover the main success flow and two primary failure cases: invalid/incomplete data and a database/storage failure. However, the use case does not explicitly include a scenario where the author saves without making any changes (no-op save) or saves with partially completed fields that are allowed for drafts depending on “partial validation rules.” Adding an extension clarifying whether partial drafts are permitted (and what minimum required fields exist) would improve completeness.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. All scenarios align with the stated goal, preconditions, and the draft-save validation/storage logic, and no undocumented functionality is introduced.