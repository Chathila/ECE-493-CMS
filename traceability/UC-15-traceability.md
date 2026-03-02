# UC-15 Traceability Matrix

- AT-01 Generate Schedule Successfully
  - `src/test/java/com/ece493/cms/acceptance/UC15AcceptanceIT.java#AT01_generateScheduleSuccessfully`
  - `src/test/java/com/ece493/cms/integration/ScheduleGenerationEndpointIT.java#generatesScheduleWhenDataIsAvailable`
- AT-02 Missing Scheduling Data (Rooms/Time Slots)
  - `src/test/java/com/ece493/cms/acceptance/UC15AcceptanceIT.java#AT02_missingSchedulingData`
  - `src/test/java/com/ece493/cms/integration/ScheduleGenerationEndpointIT.java#returnsMissingDataAndAlgorithmFailure`
- AT-03 Scheduling Algorithm Fails to Produce Valid Schedule
  - `src/test/java/com/ece493/cms/acceptance/UC15AcceptanceIT.java#AT03_algorithmFailure`
  - `src/test/java/com/ece493/cms/integration/ScheduleGenerationEndpointIT.java#returnsMissingDataAndAlgorithmFailure`
