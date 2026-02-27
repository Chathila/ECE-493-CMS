# UC-01 Acceptance Traceability

Source acceptance suite: `UC-01_tests.md`

## Mapping

| Acceptance Test | Automated Test(s) |
|---|---|
| AT-01 Successful User Registration | `src/test/java/com/ece493/cms/acceptance/UC01AcceptanceIT.java` -> `AT01_successfulUserRegistration` |
| AT-02 Missing Required Fields | `src/test/java/com/ece493/cms/acceptance/UC01AcceptanceIT.java` -> `AT02_missingRequiredFields` |
| AT-03 Invalid Email Format | `src/test/java/com/ece493/cms/acceptance/UC01AcceptanceIT.java` -> `AT03_invalidEmailFormat` |
| AT-04 Duplicate Email Address | `src/test/java/com/ece493/cms/acceptance/UC01AcceptanceIT.java` -> `AT04_duplicateEmailAddress` |
| AT-05 Weak Password | `src/test/java/com/ece493/cms/acceptance/UC01AcceptanceIT.java` -> `AT05_weakPassword` |

## Additional Supporting Tests

- Contract/endpoint behavior checks:
  - `src/test/java/com/ece493/cms/integration/RegistrationEndpointIT.java`
- Unit/business logic checks:
  - `src/test/java/com/ece493/cms/unit/RegistrationServiceImplTest.java`
  - `src/test/java/com/ece493/cms/unit/DefaultEmailValidationServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/DefaultPasswordPolicyServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/PasswordHasherTest.java`
  - `src/test/java/com/ece493/cms/unit/RegistrationServletTest.java`

## Non-automatable Steps

- None for UC-01 in this environment. All AT-01..AT-05 are fully automated.
