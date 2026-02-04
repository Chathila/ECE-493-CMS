# UC-15: Generate Conference Schedule

## Goal in Context
Allow the system to automatically generate a conference schedule so that conference sessions are organized.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Administrator

## Secondary Actors
- CMS Scheduling Algorithm  
- CMS Database

## Trigger
Administrator requests generation of the conference schedule.

## Success End Condition
A complete conference schedule is automatically generated and stored in the CMS, and the administrator can view the generated schedule.

## Failed End Condition
The conference schedule is not generated, and the system displays an appropriate error message explaining the reason for failure.

## Preconditions
- The administrator is registered and logged in to the CMS.
- All accepted papers are available in the system.
- Required scheduling data (rooms, time slots) is available.

## Main Success Scenario
1. Administrator logs in to the CMS.
2. Administrator selects the option to generate the conference schedule.
3. System retrieves accepted papers and available scheduling resources.
4. System applies the scheduling algorithm to organize sessions.
5. System generates the conference schedule.
6. System stores the generated schedule in the CMS database.
7. System displays the generated schedule to the administrator.

## Extensions
- **3a**: Required scheduling data is incomplete or missing  
  - **3a1**: System displays an error message indicating missing scheduling information.

- **4a**: Scheduling algorithm fails to generate a valid schedule  
  - **4a1**: System displays an error message and logs the failure.

## Related Information
- **Priority**: High  
- **Frequency**: Low  
- **Open Issues**: Scheduling algorithm parameters and optimization criteria may be updated in future CMS versions.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario describes a plausible administrator-initiated scheduling operation, and the extensions branch from appropriate points where required data is checked or the algorithm executes.

* Flow coverage: The scenarios cover the main success flow and two key failure cases: missing scheduling inputs and failure to produce a valid schedule. However, the use case does not include a scenario where schedule generation succeeds but storing the schedule fails due to a database error. Adding an extension for “schedule generated but cannot be saved” would improve completeness.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. All scenarios align with the goal, preconditions, and generation/storage steps, and no undocumented functionality is introduced.