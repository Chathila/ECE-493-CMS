# UC-05 Acceptance Traceability

Source acceptance suite: `UC-05_tests.md`

## Mapping

| Acceptance Test | Automated Test(s) |
|---|---|
| AT-01 Successfully Save Draft | `src/test/java/com/ece493/cms/acceptance/UC05AcceptanceIT.java` -> `AT01_successfullySaveDraft` |
| AT-02 Save Draft with Invalid or Incomplete Data | `src/test/java/com/ece493/cms/acceptance/UC05AcceptanceIT.java` -> `AT02_invalidOrIncompleteDraftData` |
| AT-03 Database/Storage Error During Save | `src/test/java/com/ece493/cms/acceptance/UC05AcceptanceIT.java` -> `AT03_databaseStorageErrorDuringSave` |

## Additional Supporting Tests

- Contract/endpoint behavior checks:
  - `src/test/java/com/ece493/cms/integration/DraftSaveEndpointIT.java`
- Unit/business logic checks:
  - `src/test/java/com/ece493/cms/unit/DraftSaveServiceImplTest.java`
  - `src/test/java/com/ece493/cms/unit/DefaultDraftValidationServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/DraftSaveServletTest.java`
  - `src/test/java/com/ece493/cms/unit/JdbcPaperSubmissionDraftRepositoryTest.java`

## Non-automatable Steps

- None for UC-05 in this environment. AT-01..AT-03 are fully automated.
