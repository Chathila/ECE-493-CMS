# UC-17: View Final Conference Schedule

## Goal in Context
Allow an attendee or author to view the final conference schedule so that they know when sessions occur.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Attendee or Author

## Secondary Actors
- CMS Scheduling Service  
- CMS Database

## Trigger
User selects the option to view the final conference schedule.

## Success End Condition
The final conference schedule is successfully displayed to the user.

## Failed End Condition
The conference schedule is not displayed, and the system shows an appropriate error message.

## Preconditions
- The final conference schedule has been published in the CMS.
- The user has access to the CMS website.

## Main Success Scenario
1. User navigates to the conference schedule section.
2. User selects the option to view the final schedule.
3. System retrieves the published schedule from the CMS database.
4. System displays the schedule in a readable format.

## Extensions
- **3a**: Final schedule is not yet published  
  - **3a1**: System displays a message indicating the schedule is not available.

- **3b**: System fails to retrieve the schedule due to a database error  
  - **3b1**: System displays an error message and requests the user to try again later.

## Related Information
- **Priority**: High  
- **Frequency**: High  
- **Open Issues**: Public access permissions for viewing the schedule may be refined in future CMS versions.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
An attendee or author accesses the CMS and navigates to the conference schedule section. The user selects the option to view the final conference schedule. The system retrieves the published schedule from the CMS database and formats it for presentation. The complete conference schedule, including session times, locations, and titles, is displayed in a clear and readable format, allowing the user to review when sessions will occur.

---

### Alternative Scenario Narrative (3a: Final Schedule Not Yet Published)
The user navigates to the conference schedule section and selects the option to view the final schedule. When the system attempts to retrieve the schedule, it determines that the final schedule has not yet been published. The system displays a message informing the user that the conference schedule is currently unavailable and advises them to check back later. No schedule details are shown.

---

### Alternative Scenario Narrative (3b: Database Retrieval Failure)
The user selects the option to view the final conference schedule. The system attempts to retrieve the published schedule from the CMS database but encounters a database error. The system displays an error message indicating that the schedule cannot be retrieved at this time and asks the user to try again later. The schedule is not displayed, and no partial data is shown.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario describes a plausible schedule-viewing interaction, and the extensions (not published, retrieval failure) branch from appropriate system retrieval steps.

* Flow coverage: The scenarios cover the main success flow and key failure cases: schedule not published and database retrieval failure. However, the use case does not include a scenario addressing access control (e.g., schedule visible publicly vs only to logged-in users), which is noted as an open issue. Adding an extension for “user lacks permission to view schedule” would improve completeness once access rules are defined.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. All scenarios align with the goal, preconditions, and retrieval/display logic, and no undocumented functionality is introduced.