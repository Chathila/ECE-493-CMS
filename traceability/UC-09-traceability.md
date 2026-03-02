# UC-09 Acceptance Traceability

Source acceptance suite: `UC-09_tests.md`

## Mapping

| Acceptance Test | Automated Test(s) |
|---|---|
| AT-01 Invitation Email Sent Successfully | `src/test/java/com/ece493/cms/acceptance/UC09AcceptanceIT.java` -> `AT01_invitationEmailSentSuccessfully` |
| AT-02 Email Service Failure | `src/test/java/com/ece493/cms/acceptance/UC09AcceptanceIT.java` -> `AT02_emailServiceFailure` |
| AT-03 Invalid Referee Email Address | `src/test/java/com/ece493/cms/acceptance/UC09AcceptanceIT.java` -> `AT03_invalidRefereeEmailAddress` |

## Additional Supporting Tests

- Contract/endpoint behavior checks:
  - `src/test/java/com/ece493/cms/integration/ReviewInvitationFlowEndpointIT.java`
- Unit/business logic checks:
  - `src/test/java/com/ece493/cms/unit/ReviewInvitationServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/InMemoryNotificationServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/DefaultInvitationValidationServiceTest.java`

## Non-automatable Steps

- None for UC-09 in this environment. AT-01..AT-03 are automated.
