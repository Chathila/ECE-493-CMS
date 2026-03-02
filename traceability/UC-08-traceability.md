# UC-08 Acceptance Traceability

Source acceptance suite: `UC-08_tests.md`

## Mapping

| Acceptance Test | Automated Test(s) |
|---|---|
| AT-01 Allow Assignment When Reviewer Has Fewer Than Five Papers | `src/test/java/com/ece493/cms/acceptance/UC08AcceptanceIT.java` -> `AT01_allowAssignmentWhenReviewerHasFewerThanFivePapers` |
| AT-02 Block Assignment When Reviewer Already Has Five Papers | `src/test/java/com/ece493/cms/acceptance/UC08AcceptanceIT.java` -> `AT02_blockAssignmentWhenReviewerAlreadyHasFivePapers` |
| AT-03 Workload Count Retrieval Failure (Database Error) | `src/test/java/com/ece493/cms/acceptance/UC08AcceptanceIT.java` -> `AT03_workloadCountRetrievalFailureBlocksAssignment` |

## Additional Supporting Tests

- Contract/endpoint behavior checks:
  - `src/test/java/com/ece493/cms/integration/WorkloadEnforcementEndpointIT.java`
- Unit/business logic checks:
  - `src/test/java/com/ece493/cms/unit/DefaultWorkloadCheckServiceTest.java`
  - `src/test/java/com/ece493/cms/unit/RefereeAssignmentServiceImplTest.java`

## Non-automatable Steps

- None for UC-08 in this environment. AT-01..AT-03 are automated.
