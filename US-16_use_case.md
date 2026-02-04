# UC-16: Edit Conference Schedule

## Goal in Context
Allow an editor to edit the automatically generated conference schedule so that it reflects final conference planning.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Editor

## Secondary Actors
- CMS Scheduling Service  
- CMS Database

## Trigger
Editor selects the option to edit the generated conference schedule.

## Success End Condition
The edited conference schedule is successfully updated in the CMS database and marked as the current final version.

## Failed End Condition
The schedule edits are not saved, and the system displays an appropriate error message explaining the reason for failure.

## Preconditions
- The editor is registered and logged in to the CMS.
- A conference schedule has already been generated.
- The schedule is available for editing.

## Main Success Scenario
1. Editor logs in to the CMS.
2. Editor navigates to the conference scheduling section.
3. Editor selects the generated conference schedule.
4. System displays the schedule in an editable format.
5. Editor modifies session times, rooms, or assignments as needed.
6. Editor submits the updated schedule.
7. System validates the edited schedule for conflicts and completeness.
8. System saves the updated schedule in the CMS database.
9. System confirms that the schedule has been successfully updated.

## Extensions
- **5a**: Editor introduces a scheduling conflict (e.g., room or time overlap)  
  - **5a1**: System highlights the conflict and requests correction.

- **7a**: Validation of the edited schedule fails  
  - **7a1**: System displays an error message indicating invalid or incomplete scheduling information.

- **8a**: System encounters a database error while saving  
  - **8a1**: System displays an error message and requests the editor to retry.

## Related Information
- **Priority**: High  
- **Frequency**: Low  
- **Open Issues**: Versioning and rollback of edited schedules may be added in future CMS versions.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
The editor logs in to the CMS and navigates to the conference scheduling section. The system presents the previously generated conference schedule in an editable format. The editor reviews the schedule and makes necessary adjustments, such as changing session times, reassigning rooms, or updating paper-to-session mappings to better reflect final planning decisions. After completing the edits, the editor submits the updated schedule. The system validates the revised schedule to ensure there are no conflicts and that all required scheduling information is complete. Once validation succeeds, the system saves the updated schedule in the CMS database, marks it as the current final version, and confirms to the editor that the schedule has been successfully updated.

---

### Alternative Scenario Narrative (5a: Scheduling Conflict Introduced)
The editor opens the generated schedule and makes changes that introduce a scheduling conflict, such as assigning two sessions to the same room at the same time. When the editor submits the updated schedule, the system detects the conflict during validation. The system highlights the conflicting sessions and displays a message explaining the issue, prompting the editor to correct the conflict before resubmitting. The schedule is not saved until the conflict is resolved.

---

### Alternative Scenario Narrative (7a: Validation Failure Due to Invalid or Incomplete Edits)
The editor edits the schedule but leaves required scheduling information incomplete or enters invalid data, such as an undefined room or invalid time slot. Upon submission, the system performs validation checks and identifies the invalid or missing information. The system displays an error message indicating what information must be corrected. The updated schedule is not saved, and the editor must revise the edits before attempting to submit again.

---

### Alternative Scenario Narrative (8a: Database Error While Saving)
The editor completes valid schedule edits and submits the updated schedule. The system successfully validates the schedule but encounters a database error while attempting to save the changes. The system displays an error message informing the editor that the update could not be saved and requests that the editor retry the operation later. The existing schedule remains unchanged in the CMS database.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario reflects a plausible schedule-editing workflow, and the extensions (conflict introduced, validation failure, database save failure) branch from appropriate steps in editing, validation, and persistence.

* Flow coverage: The scenarios cover the main success flow and key failure cases related to conflicts, invalid edits, and saving failures. However, the use case does not include a scenario where the editor makes no changes but submits anyway (no-op update) or where concurrent edits occur (another editor updates the schedule first). If concurrency/versioning is expected, adding an extension for “schedule version conflict” would improve completeness, especially given the open issue on rollback/versioning.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. All scenarios align with the goal, preconditions, and validation/save steps, and no undocumented functionality is introduced.