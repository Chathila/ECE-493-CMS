# Tasks: Validate Paper File Format and Size

**Branch**: `UC-06-validate-paper-file`  
**Plan**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-06-validate-paper-file/plan.md  
**Spec**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-06-validate-paper-file/spec.md  
**Date**: 2026-02-05

## Dependencies

- User Story 1 (P1) must be completed before User Story 2 (P2) and User Story 3
  (P3) because it establishes the core validation flow.
- User Story 2 (P2) and User Story 3 (P3) can be implemented in parallel after
  User Story 1 (P1) is complete.

## Implementation Strategy

- MVP scope: User Story 1 (core validation flow and success path).
- Incremental delivery: Add format errors (P2), then size/upload/corruption
  errors (P3).

## Phase 1: Setup

- [ ] T001 Create MVC directories per plan in src/controllers, src/models,
  src/services, src/views, src/static
- [ ] T002 Create test directories per plan in tests/contract, tests/integration,
  tests/unit

## Phase 2: Foundational

- [ ] T003 [P] Define Manuscript File model interface in
  src/models/ManuscriptFile.java
- [ ] T004 [P] Define file validation service interface in
  src/services/FileValidationService.java
- [ ] T005 [P] Define upload handling interface in
  src/services/UploadService.java

## Phase 3: User Story 1 (P1) - Validate File Successfully

**Story Goal**: Validate file format and size and allow progression when valid.

**Independent Test**: Upload compliant PDF/DOCX ≤ 20 MB and verify success.

- [ ] T006 [US1] Create file validation controller in
  src/controllers/FileValidationController.java
- [ ] T007 [US1] Implement validation handler in
  src/controllers/FileValidationController.java
- [ ] T008 [US1] Implement validation orchestration in
  src/services/FileValidationService.java
- [ ] T009 [US1] Implement validation success response in
  src/views/submit-paper.html

## Phase 4: User Story 2 (P2) - Handle Unsupported File Formats

**Story Goal**: Reject unsupported formats and show allowed formats.

**Independent Test**: Upload unsupported format and verify error message.

- [ ] T010 [US2] Add format validation (PDF/DOCX) in
  src/services/FileValidationService.java
- [ ] T011 [US2] Render unsupported format error messaging in
  src/views/submit-paper.html

## Phase 5: User Story 3 (P3) - Handle Oversized Files and Upload Failures

**Story Goal**: Reject oversized/corrupted files and handle upload failures.

**Independent Test**: Upload oversized/corrupted file or simulate failure and
verify correct errors.

- [ ] T012 [US3] Add file size validation (max 20 MB) in
  src/services/FileValidationService.java
- [ ] T013 [US3] Add corrupted/unreadable file detection in
  src/services/FileValidationService.java
- [ ] T014 [US3] Add upload failure handling in
  src/services/UploadService.java
- [ ] T015 [US3] Render file size error messaging in
  src/views/submit-paper.html
- [ ] T016 [US3] Render corrupted file error messaging in
  src/views/submit-paper.html
- [ ] T017 [US3] Render upload failure retry messaging in
  src/views/submit-paper.html

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T018 Ensure validation success and error messages align with spec wording
  in src/views/submit-paper.html
- [ ] T019 Ensure allowed formats and size limits are documented in UI helper text
  in src/views/submit-paper.html

## Parallel Execution Opportunities

- US2 tasks T010-T011 can be executed in parallel after US1.
- US3 tasks T012-T017 can be executed in parallel after US1.
- Foundational interface tasks T003-T005 can be done in parallel.

## Format Validation

- All tasks use checkbox format with IDs and file paths.
