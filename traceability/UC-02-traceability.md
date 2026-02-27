# UC-02 Acceptance Traceability

Source acceptance suite: `UC-02_tests.md`

## Mapping

| Acceptance Test | Automated Test(s) |
|---|---|
| AT-01 Successful Login with Valid Credentials | `src/test/java/com/ece493/cms/acceptance/UC02AcceptanceIT.java` -> `AT01_successfulLoginWithValidCredentials` |
| AT-02 Missing Required Fields | `src/test/java/com/ece493/cms/acceptance/UC02AcceptanceIT.java` -> `AT02_missingRequiredFields` |
| AT-03 Incorrect Email or Password | `src/test/java/com/ece493/cms/acceptance/UC02AcceptanceIT.java` -> `AT03_incorrectEmailOrPassword` |
| AT-04 Inactive or Locked Account | `src/test/java/com/ece493/cms/acceptance/UC02AcceptanceIT.java` -> `AT04_inactiveOrLockedAccount` |

## Additional Supporting Tests

- Contract/endpoint behavior checks:
  - `src/test/java/com/ece493/cms/integration/LoginEndpointIT.java`
- Unit/business logic checks:
  - `src/test/java/com/ece493/cms/unit/AuthenticationServiceImplTest.java`
  - `src/test/java/com/ece493/cms/unit/LoginServletTest.java`
  - `src/test/java/com/ece493/cms/unit/JdbcUserAccountRepositoryTest.java`
  - `src/test/java/com/ece493/cms/unit/LoginResultTest.java`

## Non-automatable Steps

- None for UC-02 in this environment. All AT-01..AT-04 are fully automated.
