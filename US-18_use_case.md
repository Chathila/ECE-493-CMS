# UC-18: View Conference Registration Prices

## Goal in Context
Allow a guest or registered user to view conference registration prices so that they can decide whether to attend the conference.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Guest or Registered User

## Secondary Actors
- CMS Pricing Service  
- CMS Database

## Trigger
User selects the option to view conference registration prices.

## Success End Condition
The conference registration price list is successfully displayed to the user.

## Failed End Condition
The registration prices are not displayed, and the system shows an appropriate error message.

## Preconditions
- Conference registration pricing information exists in the CMS.
- The CMS website is accessible.

## Main Success Scenario
1. User navigates to the conference registration or information section.
2. User selects the option to view registration prices.
3. System retrieves the registration price list from the CMS database.
4. System displays the registration prices based on attendee categories.

## Extensions
- **3a**: Registration pricing information is not available  
  - **3a1**: System displays a message indicating that pricing information is currently unavailable.

- **3b**: System fails to retrieve pricing information due to a database error  
  - **3b1**: System displays an error message and requests the user to try again later.

## Related Information
- **Priority**: Medium  
- **Frequency**: High  
- **Open Issues**: Pricing categories and discount rules may be updated in future CMS versions.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
A guest or registered user accesses the CMS and navigates to the conference registration or information section. The user selects the option to view conference registration prices. The system retrieves the current registration pricing information from the CMS database via the pricing service. The system then displays a clear and organized list of registration prices, grouped by attendee categories (such as regular, student, or author), allowing the user to review costs and decide whether to attend the conference.

---

### Alternative Scenario Narrative (3a: Registration Pricing Information Not Available)
The user navigates to the registration or information section and selects the option to view conference registration prices. When the system attempts to retrieve pricing data, it determines that registration pricing information has not yet been configured or published. The system displays a message informing the user that pricing information is currently unavailable and advises them to check back later. No pricing details are displayed.

---

### Alternative Scenario Narrative (3b: Database Retrieval Failure)
The user selects the option to view conference registration prices. The system attempts to retrieve the pricing information from the CMS database but encounters a database error. The system displays an error message indicating that registration prices cannot be retrieved at this time and requests the user to try again later. The pricing information is not displayed.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario is a plausible read-only interaction for viewing pricing information, and the extensions branch appropriately from the retrieval step.

* Flow coverage: The scenarios cover the main success flow and key failure cases: pricing not available and database retrieval failure. However, the use case does not include a scenario where pricing exists but the user filters/selects a category (e.g., student/regular) or where outdated pricing is detected/flagged. If category selection or validity periods are part of the intended behavior, adding an extension for “category not found/invalid” or “pricing expired” would improve completeness.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. All scenarios align with the goal, preconditions, and retrieval/display steps, and no undocumented functionality is introduced.