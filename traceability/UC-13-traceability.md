# UC-13 Traceability Matrix

- AT-01 Record Acceptance Decision Successfully
  - `src/test/java/com/ece493/cms/acceptance/UC13AcceptanceIT.java#AT01_recordAcceptanceDecisionSuccessfully`
  - `src/test/java/com/ece493/cms/integration/FinalDecisionEndpointIT.java#recordsFinalDecisionWhenAllReviewsAreSubmitted`
- AT-02 Record Rejection Decision Successfully
  - `src/test/java/com/ece493/cms/acceptance/UC13AcceptanceIT.java#AT02_recordRejectionDecisionSuccessfully`
- AT-03 Attempt Decision Before All Reviews Completed
  - `src/test/java/com/ece493/cms/acceptance/UC13AcceptanceIT.java#AT03_attemptDecisionBeforeAllReviewsCompleted`
  - `src/test/java/com/ece493/cms/integration/FinalDecisionEndpointIT.java#blocksDecisionWhenReviewsIncompleteAndHandlesSaveFailure`
- AT-04 Database Error While Recording Decision
  - `src/test/java/com/ece493/cms/acceptance/UC13AcceptanceIT.java#AT04_databaseErrorWhileRecordingDecision`
  - `src/test/java/com/ece493/cms/integration/FinalDecisionEndpointIT.java#blocksDecisionWhenReviewsIncompleteAndHandlesSaveFailure`
