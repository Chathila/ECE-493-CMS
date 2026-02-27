# UC-03 Acceptance Traceability

Source acceptance suite: `UC-03_tests.md`

## Mapping

| Acceptance Test | Automated Test(s) |
|---|---|
| AT-01 Successful Password Change | `src/test/java/com/ece493/cms/acceptance/UC03AcceptanceIT.java` -> `AT01_successfulPasswordChange` |
| AT-02 Incorrect Current Password | `src/test/java/com/ece493/cms/acceptance/UC03AcceptanceIT.java` -> `AT02_incorrectCurrentPassword` |
| AT-03 New Password Does Not Meet Security Requirements | `src/test/java/com/ece493/cms/acceptance/UC03AcceptanceIT.java` -> `AT03_newPasswordDoesNotMeetSecurityRequirements` |
| AT-04 New Password and Confirmation Do Not Match | `src/test/java/com/ece493/cms/acceptance/UC03AcceptanceIT.java` -> `AT04_newPasswordAndConfirmationDoNotMatch` |

## Additional Supporting Tests

- Contract/endpoint behavior checks:
  - `src/test/java/com/ece493/cms/integration/ChangePasswordEndpointIT.java`
- Unit/business logic checks:
  - `src/test/java/com/ece493/cms/unit/PasswordChangeServiceImplTest.java`
  - `src/test/java/com/ece493/cms/unit/ChangePasswordServletTest.java`
  - `src/test/java/com/ece493/cms/unit/JdbcUserAccountRepositoryTest.java`

## Non-automatable Steps

- None for UC-03 in this environment. All AT-01..AT-04 are fully automated.
