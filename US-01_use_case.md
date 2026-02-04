# UC-01: Register User Account

**Goal in Context**: Allow a new user to create an account using an email address and password in order to access CMS features.
**Scope**: Conference Management System (CMS)
**Level**: User Goal
**Primary Actor**: User
**Secondary Actors**: Email Validation Service, CMS Database

**Trigger**: User selects the option to register on the CMS website.

## Success End Condition
* The user account is successfully created and stored in the CMS database, and the user is redirected to the login page.

## Failed End Condition
* The user account is not created, and the system displays an appropriate error message explaining the reason for failure.

## Preconditions
* The user is not already registered in the CMS.
* The CMS registration page is available.

## Main Success Scenario
* **Step 1**: User selects the Register option on the CMS homepage.
* **Step 2**:System displays the registration form.
* **Step 3**:User enters a valid email address and password.
* **Step 4**:User submits the registration form.
* **Step 5**:System validates the email format, checks email uniqueness, and verifies password security requirements.
* **Step 6**:System stores the user information in the CMS database.
* **Step 7**:System displays a confirmation message.
* **Step 8**:System redirects the user to the login page.

## Extensions
* 3a: User leaves one or more required fields empty
* 3a1: System displays an error message indicating missing required fields.
* 5a: Email address is invalid or already registered
* 5a1: System displays an error message requesting a valid, unique email address.
* 5b: Password does not meet security standards
* 5b1: System displays password requirements and requests the user to re-enter a valid password.

Related Information
* **Priority**: High
* **Frequency**: High
* **Open Issues**: Password security requirements may be updated in future versions of the CMS.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
A new user visits the CMS homepage and selects the **Register** option to create an account. The system displays the registration form requesting an email address and password. The user enters a valid email address that is not already registered and provides a password that meets the CMS security requirements. After submitting the form, the system validates the email format, confirms the email is unique, and verifies the password strength. Once all validations pass, the system stores the new user’s information in the CMS database, displays a confirmation message, and redirects the user to the login page so they can access CMS features.

---

### Alternative Scenario Narrative (3a: Missing Required Fields)
A user opens the registration form but leaves one or more required fields, such as the email address or password, empty. When the user submits the form, the system detects the missing information and displays an error message indicating that all required fields must be completed. The system does not create an account, and the user remains on the registration page to correct the input.

---

### Alternative Scenario Narrative (5a: Invalid or Duplicate Email Address)
A user attempts to register by entering an email address that is either incorrectly formatted or already associated with an existing CMS account. Upon form submission, the system validates the email and identifies the issue. The system displays an error message requesting a valid and unique email address and prevents the account from being created until the issue is resolved.

---

### Alternative Scenario Narrative (5b: Weak Password)
A user completes the registration form with a valid email address but provides a password that does not meet the CMS security standards. After submission, the system evaluates the password, determines that it does not satisfy the required criteria, and displays the password requirements. The account is not created, and the user is prompted to enter a stronger password before resubmitting the form.

## Use case analysis
* Scenario validity and plausibility: The scenario is a valid instantiation of documented flows in the use case. The main success scenario and each extension branch from appropriate steps in the flow, and the system behavior described is plausible and consistent with typical web-based registration systems.

* Flow coverage: The scenarios cover the main success flow and key validation failures . However, the use case does not include a scenario for user abandonment prior to submission. Adding an extension to capture the user cancelling or leaving the registration process would improve completeness.

* Undocumented or extra flows: No scenarios represent flows outside those documented or implied by the use case. All scenarios align with the stated goal, preconditions, and validation logic, and no additional undocumented functionality is introduced.