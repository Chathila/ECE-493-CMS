# UC-02: User Login

## Goal in Context
Allow a registered user to authenticate using valid credentials in order to access authorized CMS features.

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
Registered user selects the **Log In** option on the CMS website.

## Success End Condition
The user is successfully authenticated and redirected to their authorized home page with access to role-specific CMS features.

## Failed End Condition
The user is not authenticated, and the system displays an appropriate error message explaining the reason for failure.

## Preconditions
- The user has an existing registered account in the CMS.
- The CMS login page is available.

## Main Success Scenario
1. Registered user selects the **Log In** option on the CMS homepage.
2. System displays the login form requesting username (email) and password.
3. User enters their registered email address and password.
4. User submits the login form.
5. System validates the provided credentials against the CMS database.
6. System authenticates the user.
7. System redirects the user to their authorized home page.

## Extensions
- **3a**: User leaves one or more required fields empty  
  - **3a1**: System displays an error message indicating missing required fields.

- **5a**: Email address or password is incorrect  
  - **5a1**: System displays an authentication failure message and prompts the user to retry.

- **5b**: User account is inactive or locked  
  - **5b1**: System displays an account status message and denies access.

## Related Information
- **Priority**: High  
- **Frequency**: Very High  
- **Open Issues**: Account lockout policy after repeated failed login attempts is not defined.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
A registered user visits the CMS homepage and selects the **Log In** option. The system displays a login form requesting the user’s email address and password. The user enters their registered credentials and submits the form. The system validates the provided information against the CMS database and successfully authenticates the user. Once authentication is complete, the system redirects the user to their authorized home page, where they can access CMS features permitted by their role.

---

### Alternative Scenario Narrative (3a: Missing Required Fields)
A registered user opens the login form but leaves one or more required fields, such as the email address or password, empty. When the user submits the form, the system detects the missing information and displays an error message indicating that all required fields must be completed. The user is not authenticated and remains on the login page.

---

### Alternative Scenario Narrative (5a: Incorrect Email or Password)
A registered user attempts to log in by entering an incorrect email address or an incorrect password. After the form is submitted, the system validates the credentials and determines that they do not match any valid account. The system displays an authentication failure message and prompts the user to retry entering their credentials. Access to the CMS is denied until valid credentials are provided.

---

### Alternative Scenario Narrative (5b: Inactive or Locked Account)
A user attempts to log in using credentials for an account that is marked as inactive or locked in the CMS. When the system validates the credentials, it detects the account’s status and denies authentication. The system displays a message explaining that the account is inactive or locked and prevents access to CMS features.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario and each extension branch from appropriate steps in the authentication process, and the described system behavior is plausible for a standard login mechanism.

* Flow coverage: The scenarios cover the main success flow and common failure cases, including missing credentials, incorrect credentials, and inactive or locked accounts. However, the use case does not explicitly include a scenario for repeated failed login attempts leading to account lockout, which is noted as an open issue. Adding a future extension for this case would improve completeness once the policy is defined.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. All scenarios align with the stated goal, preconditions, and authentication logic, and no undocumented functionality is introduced.