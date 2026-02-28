# UC-04 Acceptance Traceability

Source acceptance suite: `UC-04_tests.md`

## Mapping

| Acceptance Test | Automated Test(s) |
|---|---|
| AT-01 Successful Paper Submission | `src/test/java/com/ece493/cms/acceptance/UC04AcceptanceIT.java` -> `AT01_successfulPaperSubmission` |
| AT-02 Missing Required Metadata | `src/test/java/com/ece493/cms/acceptance/UC04AcceptanceIT.java` -> `AT02_missingRequiredMetadata` |
| AT-03 Unsupported File Format | `src/test/java/com/ece493/cms/acceptance/UC04AcceptanceIT.java` -> `AT03_unsupportedFileFormat` |
| AT-04 File Size Exceeds Limit | `src/test/java/com/ece493/cms/acceptance/UC04AcceptanceIT.java` -> `AT04_fileSizeExceedsLimit` |
| AT-05 Invalid Form Data | `src/test/java/com/ece493/cms/acceptance/UC04AcceptanceIT.java` -> `AT05_invalidFormData` |

## Additional Supporting Tests

- Contract/endpoint behavior checks:
  - `src/test/java/com/ece493/cms/integration/PaperSubmissionEndpointIT.java`
- Unit/business logic checks:
  - `src/test/java/com/ece493/cms/unit/PaperSubmissionServiceImplTest.java`
  - `src/test/java/com/ece493/cms/unit/PaperSubmissionServletTest.java`
  - `src/test/java/com/ece493/cms/unit/DefaultMetadataValidationServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/InMemoryFileStorageServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/JdbcPaperSubmissionRepositoryTest.java`

## Upload Failure Coverage

- Optional operational flow from `UC-04_tests.md` analysis (`AT-06` style upload/storage failure) is automated in:
  - `src/test/java/com/ece493/cms/integration/PaperSubmissionEndpointIT.java` -> `postSubmitPaperUploadFailureReturns503`

## Non-automatable Steps

- None for UC-04 in this environment. AT-01..AT-05 are fully automated.
