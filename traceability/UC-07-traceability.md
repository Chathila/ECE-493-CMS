# UC-07 Acceptance Traceability

Source acceptance suite: `UC-07_tests.md`

## Mapping

| Acceptance Test | Automated Test(s) |
|---|---|
| AT-01 Successfully Assign Referees and Send Invitations | `src/test/java/com/ece493/cms/acceptance/UC07AcceptanceIT.java` -> `AT01_successfullyAssignRefereesAndSendInvitations` |
| AT-02 Invalid Referee Email Address | `src/test/java/com/ece493/cms/acceptance/UC07AcceptanceIT.java` -> `AT02_invalidRefereeEmailAddress` |
| AT-03 Referee Workload Limit Exceeded | `src/test/java/com/ece493/cms/acceptance/UC07AcceptanceIT.java` -> `AT03_refereeWorkloadLimitExceeded` |
| AT-04 Too Many Referees Assigned to a Paper | `src/test/java/com/ece493/cms/acceptance/UC07AcceptanceIT.java` -> `AT04_tooManyRefereesAssignedToPaper_conflictWithClarificationNoMaximum` |

## Additional Supporting Tests

- Contract/endpoint behavior checks:
  - `src/test/java/com/ece493/cms/integration/RefereeAssignmentEndpointIT.java`
- Unit/business logic checks:
  - `src/test/java/com/ece493/cms/unit/RefereeAssignmentServiceImplTest.java`
  - `src/test/java/com/ece493/cms/unit/RefereeAssignmentServletTest.java`
  - `src/test/java/com/ece493/cms/unit/JdbcRefereeAssignmentRepositoryTest.java`

## Conflicts / Ambiguities

- `UC-07_tests.md` AT-04 expects a failure when assigning more than the allowed referee count.
- `specs/UC-07-assign-referees/clarifications.md` states there is no maximum number of referees per paper.
- Automation follows the UC-07 clarification (no maximum), so AT-04 is implemented as a documented conflict check rather than a blocking error-path check.

## Non-automatable Steps

- None for UC-07 in this environment. AT-01..AT-04 are automated with the clarification conflict documented above.
