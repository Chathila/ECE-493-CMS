# UC-10 Acceptance Traceability

Source acceptance suite: `UC-10_tests.md`

## Mapping

| Acceptance Test | Automated Test(s) |
|---|---|
| AT-01 Accept Invitation Successfully | `src/test/java/com/ece493/cms/acceptance/UC10AcceptanceIT.java` -> `AT01_acceptInvitationSuccessfully` |
| AT-02 Reject Invitation Successfully | `src/test/java/com/ece493/cms/acceptance/UC10AcceptanceIT.java` -> `AT02_rejectInvitationSuccessfully` |
| AT-03 Attempt to Respond After Invitation Expired | `src/test/java/com/ece493/cms/acceptance/UC10AcceptanceIT.java` -> `AT03_attemptToRespondAfterInvitationExpired` |
| AT-04 Database Error When Recording Response | `src/test/java/com/ece493/cms/acceptance/UC10AcceptanceIT.java` -> `AT04_databaseErrorWhenRecordingResponse` |

## Additional Supporting Tests

- Contract/endpoint behavior checks:
  - `src/test/java/com/ece493/cms/integration/InvitationResponseEndpointIT.java`
- Unit/business logic checks:
  - `src/test/java/com/ece493/cms/unit/InvitationResponseServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/InvitationResponseServletTest.java`
  - `src/test/java/com/ece493/cms/unit/ReviewAssignmentServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/ReviewInvitationTest.java`
  - `src/test/java/com/ece493/cms/unit/InMemoryInvitationResponseRepositoryTest.java`
  - `src/test/java/com/ece493/cms/unit/InMemoryReviewInvitationRepositoryTest.java`

## Non-automatable Steps

- None for UC-10 in this environment. AT-01..AT-04 are automated.
