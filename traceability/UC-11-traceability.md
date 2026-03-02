# UC-11 Acceptance Traceability

Source acceptance suite: `UC-11_tests.md`

## Mapping

| Acceptance Test | Automated Test(s) |
|---|---|
| AT-01 Access Review Form Successfully | `src/test/java/com/ece493/cms/acceptance/UC11AcceptanceIT.java` -> `AT01_accessReviewFormSuccessfully` |
| AT-02 Access Denied for Unauthorized Paper | `src/test/java/com/ece493/cms/acceptance/UC11AcceptanceIT.java` -> `AT02_accessDeniedForUnauthorizedPaper` |
| AT-03 Review Form Retrieval Failure (System/Database Error) | `src/test/java/com/ece493/cms/acceptance/UC11AcceptanceIT.java` -> `AT03_reviewFormRetrievalFailure` |

## Additional Supporting Tests

- Endpoint behavior:
  - `src/test/java/com/ece493/cms/integration/ReviewFormEndpointIT.java`
- Service/authorization/unit behavior:
  - `src/test/java/com/ece493/cms/unit/ReviewFormServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/ReviewAuthorizationServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/InMemoryReviewFormRepositoryTest.java`
  - `src/test/java/com/ece493/cms/unit/InMemoryReviewAssignmentRepositoryTest.java`
  - `src/test/java/com/ece493/cms/unit/ReviewWorkflowServletTest.java`

## Non-automatable Steps

- None for UC-11 in this environment.
