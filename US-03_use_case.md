# UC-03: Change User Password

## Goal in Context
Allow a registered user to change their account password in order to maintain account security.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Registered User

## Secondary Actors
- CMS Authentication Service  
- CMS Database

## Trigger
Registered user selects the **Change Password** option from their account settings.

## Success End Condition
The user’s password is successfully updated in the CMS database, and the user receives confirmation of the change.

## Failed End Condition
The password is not updated, and the system displays an appropriate error message explaining the reason for failure.

## Preconditions
- The user is logged in to the CMS.
- The user has a valid registered account.

## Main Success Scenario
1. Registered user navigates to account settings.
2. User selects the **Change Password** option.
3. System displays the change password form.
4. User enters their current password.
5. User enters a new password that meets security requirements.
6. User confirms the new password.
7. User submits the change password request.
8. System verifies the current password.
9. System validates the new password against security standards.
10. System updates the password in the CMS database.
11. System displays a confirmation message indicating the password was successfully changed.

## Extensions
- **4a**: Current password is incorrect  
  - **4a1**: System displays an error message indicating the current password is invalid.

- **5a**: New password does not meet security requirements  
  - **5a1**: System displays password guidelines and requests re-entry.

- **6a**: New password and confirmation do not match  
  - **6a1**: System displays an error message requesting matching passwords.

## Related Information
- **Priority**: High  
- **Frequency**: Medium  
- **Open Issues**: Password security standards may be updated in future CMS releases.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
A registered user who is currently logged in navigates to their account settings within the CMS and selects the **Change Password** option. The system displays a form requesting the user’s current password, a new password, and a confirmation of the new password. The user enters their correct current password and provides a new password that satisfies the CMS security requirements, then confirms it and submits the request. The system verifies the current password, validates the new password against security standards, and updates the password in the CMS database. Once the update is successful, the system displays a confirmation message indicating that the password has been changed.

---

### Alternative Scenario Narrative (4a: Incorrect Current Password)
A logged-in user attempts to change their password but enters an incorrect current password in the change password form. After the user submits the request, the system verifies the current password and detects that it does not match the stored credentials. The system displays an error message indicating that the current password is invalid and does not update the password. The user remains on the change password page and may retry with the correct current password.

---

### Alternative Scenario Narrative (5a: New Password Does Not Meet Security Requirements)
A logged-in user enters the correct current password but provides a new password that does not meet the CMS security requirements, such as minimum length or complexity rules. When the form is submitted, the system validates the new password, determines that it is insufficient, and displays password guidelines to the user. The password is not updated, and the user is prompted to re-enter a compliant new password.

---

### Alternative Scenario Narrative (6a: New Password and Confirmation Do Not Match)
A logged-in user enters a correct current password and a new password, but the confirmation password does not match the new password. Upon submission, the system detects the mismatch and displays an error message requesting that the passwords match. The system does not update the password, and the user must correct the input before resubmitting the request.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario and each extension branch from appropriate steps in the password change process, and the described behavior is plausible for a standard account security feature.

* Flow coverage: The scenarios cover the main success flow and common failure cases, including incorrect current password, weak new password, and mismatched password confirmation. The set of scenarios provides sufficient coverage for the use case as currently specified.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. All scenarios align with the stated goal, preconditions, and password validation logic, and no undocumented functionality is introduced.