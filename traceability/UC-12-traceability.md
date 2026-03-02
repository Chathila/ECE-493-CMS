# UC-12 Acceptance Traceability

Source acceptance suite: `UC-12_tests.md`

## Mapping

| Acceptance Test | Automated Test(s) |
|---|---|
| AT-01 Submit Completed Review Successfully | `src/test/java/com/ece493/cms/acceptance/UC12AcceptanceIT.java` -> `AT01_submitCompletedReviewSuccessfully` |
| AT-02 Submit Review with Missing or Invalid Required Fields | `src/test/java/com/ece493/cms/acceptance/UC12AcceptanceIT.java` -> `AT02_submitReviewWithInvalidFields` |
| AT-03 Database/Storage Error During Review Submission | `src/test/java/com/ece493/cms/acceptance/UC12AcceptanceIT.java` -> `AT03_storageFailureDuringSubmission` |

## Additional Supporting Tests

- Endpoint behavior:
  - `src/test/java/com/ece493/cms/integration/ReviewSubmissionEndpointIT.java`
- Service/validation/unit behavior:
  - `src/test/java/com/ece493/cms/unit/ReviewSubmissionServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/DefaultReviewValidationServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/InMemoryReviewRepositoryTest.java`
  - `src/test/java/com/ece493/cms/unit/ReviewWorkflowServletTest.java`

## Non-automatable Steps

- None for UC-12 in this environment.
