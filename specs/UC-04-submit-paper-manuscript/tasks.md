# Tasks: Submit Paper Manuscript

**Branch**: `UC-04-submit-paper-manuscript`  
**Plan**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-04-submit-paper-manuscript/plan.md  
**Spec**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-04-submit-paper-manuscript/spec.md  
**Date**: 2026-02-05

## Dependencies

- User Story 1 (P1) must be completed before User Story 2 (P2) and User Story 3
  (P3) because it establishes the core submission flow and storage.
- User Story 2 (P2) and User Story 3 (P3) can be implemented in parallel after
  User Story 1 (P1) is complete.

## Implementation Strategy

- MVP scope: User Story 1 (core submission flow with confirmation and redirect).
- Incremental delivery: Add metadata validation errors (P2), then file validation
  errors and upload failure handling (P3).

## Phase 1: Setup

- [ ] T001 Create MVC directories per plan in src/controllers, src/models,
  src/services, src/views, src/static
- [ ] T002 Create test directories per plan in tests/contract, tests/integration,
  tests/unit

## Phase 2: Foundational

- [ ] T003 [P] Define Paper Submission model interface in
  src/models/PaperSubmission.java
- [ ] T004 [P] Define Manuscript File model interface in
  src/models/ManuscriptFile.java
- [ ] T005 [P] Define submission service interface in
  src/services/PaperSubmissionService.java
- [ ] T006 [P] Define file storage service interface in
  src/services/FileStorageService.java
- [ ] T007 [P] Define metadata validation service interface in
  src/services/MetadataValidationService.java

## Phase 3: User Story 1 (P1) - Submit Paper Successfully

**Story Goal**: Allow authors to submit a paper with metadata and a manuscript
file, store it, confirm success, and redirect.

**Independent Test**: Submit valid metadata and a supported file and verify
storage plus confirmation/redirect.

- [ ] T008 [US1] Create submission controller in
  src/controllers/PaperSubmissionController.java
- [ ] T009 [US1] Implement submission form view in
  src/views/submit-paper.html
- [ ] T010 [US1] Wire submission route and handlers in
  src/controllers/PaperSubmissionController.java
- [ ] T011 [US1] Implement submission orchestration in
  src/services/PaperSubmissionService.java
- [ ] T012 [US1] Implement metadata persistence stub in
  src/models/PaperSubmission.java
- [ ] T013 [US1] Implement file storage stub in src/models/ManuscriptFile.java
- [ ] T014 [US1] Implement confirmation message and dashboard redirect in
  src/views/submit-paper.html

## Phase 4: User Story 2 (P2) - Handle Missing or Invalid Metadata

**Story Goal**: Block submissions with missing or invalid metadata and show
errors.

**Independent Test**: Submit missing/invalid metadata and verify errors with no
submission stored.

- [ ] T015 [US2] Add required-field validation in
  src/services/MetadataValidationService.java
- [ ] T016 [US2] Add metadata formatting validation in
  src/services/MetadataValidationService.java
- [ ] T017 [US2] Surface metadata validation errors in
  src/services/PaperSubmissionService.java
- [ ] T018 [US2] Render missing/invalid metadata errors in
  src/views/submit-paper.html

## Phase 5: User Story 3 (P3) - Handle File Validation Errors

**Story Goal**: Enforce file format/size rules and handle upload failures.

**Independent Test**: Submit unsupported/oversized file or simulate upload
failure and verify correct errors and no submission stored.

- [ ] T019 [US3] Add file format validation (PDF/DOCX) in
  src/services/FileStorageService.java
- [ ] T020 [US3] Add file size validation (max 20 MB) in
  src/services/FileStorageService.java
- [ ] T021 [US3] Add upload failure handling in
  src/services/PaperSubmissionService.java
- [ ] T022 [US3] Render unsupported format error messaging in
  src/views/submit-paper.html
- [ ] T023 [US3] Render file size limit error messaging in
  src/views/submit-paper.html
- [ ] T024 [US3] Render upload failure error messaging in
  src/views/submit-paper.html

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T025 Ensure success and error messages align with spec wording in
  src/views/submit-paper.html
- [ ] T026 Ensure allowed formats (PDF/DOCX) and size limit (20 MB) are documented
  in UI helper text in src/views/submit-paper.html
- [ ] T027 Ensure file upload failure message instructs retry in
  src/views/submit-paper.html

## Parallel Execution Opportunities

- US2 tasks T015-T018 can be executed in parallel after US1.
- US3 tasks T019-T024 can be executed in parallel after US1.
- Foundational interface tasks T003-T007 can be done in parallel.

## Format Validation

- All tasks use checkbox format with IDs and file paths.
