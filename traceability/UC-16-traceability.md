# UC-16 Traceability Matrix

- AT-01 Edit and Save Schedule Successfully
  - `src/test/java/com/ece493/cms/acceptance/UC16AcceptanceIT.java#AT01_editAndSaveScheduleSuccessfully`
  - `src/test/java/com/ece493/cms/integration/ScheduleEditEndpointIT.java#updatesScheduleAndCanRetrieveIt`
- AT-02 Scheduling Conflict Introduced (Overlap)
  - `src/test/java/com/ece493/cms/acceptance/UC16AcceptanceIT.java#AT02_schedulingConflictIntroduced`
  - `src/test/java/com/ece493/cms/integration/ScheduleEditEndpointIT.java#returnsValidationAndSaveErrors`
- AT-03 Invalid or Incomplete Schedule Edit
  - `src/test/java/com/ece493/cms/acceptance/UC16AcceptanceIT.java#AT03_invalidOrIncompleteScheduleEdit`
- AT-04 Database Error While Saving Schedule
  - `src/test/java/com/ece493/cms/acceptance/UC16AcceptanceIT.java#AT04_databaseErrorWhileSavingSchedule`
  - `src/test/java/com/ece493/cms/integration/ScheduleEditEndpointIT.java#returnsValidationAndSaveErrors`
