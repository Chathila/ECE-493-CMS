# UC-02 Implementation Report — User Login

## 1) Summary of feature implemented
Implemented UC-02 (User Login) with role-aware authenticated routing and full error handling:
- Added Log In form and POST handling (`/login`)
- Valid credentials authenticate and redirect to authorized home page (`/dashboard?role=...`)
- Missing required fields return clear validation error
- Incorrect credentials return authentication failure message
- Inactive/locked account credentials are denied with status message
- Authentication service outage returns try-again-later error
- Preserved no-plaintext password handling by verifying against hashed+salted password

## 2) Files modified/created
### Created
- `src/main/java/cms/controllers/DashboardController.java`
- `src/main/java/cms/models/AccountStatus.java`
- `src/main/java/cms/models/UserRole.java`
- `src/main/java/cms/models/LoginRequest.java`
- `src/main/java/cms/models/LoginResult.java`
- `src/main/java/cms/services/AuthenticationMessages.java`
- `src/main/java/cms/services/AuthenticationService.java`
- `src/main/java/cms/services/AuthenticationServiceImpl.java`
- `src/main/java/cms/services/AuthenticationServiceUnavailableException.java`
- `src/test/java/cms/integration/ControllerTestSupport.java`
- `src/test/java/cms/integration/LoginControllerIntegrationTest.java`
- `src/test/java/cms/unit/AuthenticationServiceImplTest.java`
- `src/test/java/cms/acceptance/UC02AcceptanceTest.java`
- `src/test/java/cms/unit/CmsPasswordPolicyServiceTest.java`
- `src/test/java/cms/unit/DefaultEmailValidationServiceTest.java`

### Modified
- `src/main/java/cms/app/CmsApplication.java`
- `src/main/java/cms/controllers/LoginController.java`
- `src/main/java/cms/models/UserAccount.java`
- `src/main/java/cms/services/PasswordHasher.java`
- `src/main/java/cms/services/Pbkdf2PasswordHasher.java`
- `src/main/java/cms/views/HtmlPageRenderer.java`
- `src/test/java/cms/integration/HttpTestSupport.java`
- `src/test/java/cms/acceptance/UC01AcceptanceTest.java`
- `src/test/java/cms/integration/RegistrationControllerIntegrationTest.java`
- `src/test/java/cms/unit/RegistrationServiceImplTest.java`

## 3) Traceability mapping
| SRS/Spec Requirement | Use Case | Code Module | Test Case |
|---|---|---|---|
| Provide Log In option + login form | UC-02 FR-001, FR-002, FR-002a | `HtmlPageRenderer.loginPage`, `LoginController` | `LoginControllerIntegrationTest.getLoginRendersFormAndHints` |
| Validate required fields | UC-02 FR-003, FR-007 | `AuthenticationServiceImpl` | `AuthenticationServiceImplTest.missingRequiredFieldsReturnsError` |
| Validate credentials against stored accounts | UC-02 FR-004, FR-005 | `AuthenticationServiceImpl`, `PasswordHasher.verify` | `AuthenticationServiceImplTest.validCredentialsAuthenticate` |
| Redirect successful login to authorized home page | UC-02 FR-006 | `LoginController`, `DashboardController` | `LoginControllerIntegrationTest.postLoginWithValidCredentialsRedirectsToDashboard`, `UC02AcceptanceTest.at01_successfulLogin` |
| Incorrect credentials error | UC-02 FR-008 | `AuthenticationServiceImpl`, `HtmlPageRenderer` | `AuthenticationServiceImplTest.invalidCredentialsDenied`, `UC02AcceptanceTest.at03_incorrectCredentials` |
| Inactive/locked account denial | UC-02 FR-009 | `AuthenticationServiceImpl`, `UserAccount(AccountStatus)` | `AuthenticationServiceImplTest.inactiveOrLockedDenied`, `UC02AcceptanceTest.at04_lockedOrInactiveDenied` |
| No plaintext passwords | UC-02 FR-010 | `Pbkdf2PasswordHasher`, `PasswordHasher.verify` | `AuthenticationServiceImplTest.validCredentialsAuthenticate` |
| Service outage handling | UC-02 FR-011 | `AuthenticationServiceImpl` + `AuthenticationServiceUnavailableException` + `LoginController` | `UC02AcceptanceTest.at05_authServiceUnavailable` |

## 4) Coverage report
- Executed: `mvn test` ✅
- Executed: `mvn verify` ✅
- JaCoCo report generated at: `target/site/jacoco/index.html`
- Branch coverage gate enforced by Maven/JaCoCo config and passed for current scoped classes.

## 5) Acceptance test results
UC-02 acceptance scenarios implemented and passing:
- AT-01 Successful login redirects authorized user ✅
- AT-02 Missing fields returns validation error ✅
- AT-03 Incorrect credentials denied with clear message ✅
- AT-04 Inactive/locked account denied with account status message ✅
- AT-05 Authentication service unavailable shows try-again-later ✅

## 6) Assumptions made
1. Email remains the login identifier (username) per UC-02 clarifications.
2. Authorized home page is represented by `/dashboard` with role context in query string for this increment.
3. No lockout/throttling is implemented for repeated failures per UC-02 clarification.
4. Authentication service outage is represented through explicit service exception in application layer.

## 7) Risk / technical debt introduced
- Session management is not yet introduced (redirect-based success only).
- Role-based authorization is currently display-level for dashboard and will need stronger access controls in later UCs.
- Account status model introduced now; persistence backing remains in-memory until later data-layer scope.
