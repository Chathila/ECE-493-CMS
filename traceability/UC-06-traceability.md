# UC-06 Acceptance Traceability

Source acceptance suite: `UC-06_tests.md`

## Mapping

| Acceptance Test | Automated Test(s) |
|---|---|
| AT-01 Accept Valid File Format and Size | `src/test/java/com/ece493/cms/acceptance/UC06AcceptanceIT.java` -> `AT01_acceptValidFileFormatAndSize` |
| AT-02 Reject Unsupported File Format | `src/test/java/com/ece493/cms/acceptance/UC06AcceptanceIT.java` -> `AT02_rejectUnsupportedFileFormat` |
| AT-03 Reject File That Exceeds Size Limit | `src/test/java/com/ece493/cms/acceptance/UC06AcceptanceIT.java` -> `AT03_rejectFileThatExceedsSizeLimit` |
| AT-04 Upload Failure (Network/System Error) | `src/test/java/com/ece493/cms/acceptance/UC06AcceptanceIT.java` -> `AT04_uploadFailureNetworkSystemError` |

## Additional Supporting Tests

- Contract/endpoint behavior checks:
  - `src/test/java/com/ece493/cms/integration/FileValidationEndpointIT.java`
- Unit/business logic checks:
  - `src/test/java/com/ece493/cms/unit/FileValidationServiceImplTest.java`
  - `src/test/java/com/ece493/cms/unit/FileValidationServletTest.java`

## Non-automatable Steps

- None for UC-06 in this environment. AT-01..AT-04 are fully automated.
