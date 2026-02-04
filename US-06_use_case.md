# UC-06: Validate Paper File Format and Size

## Goal in Context
Ensure that an uploaded paper manuscript meets conference requirements for file format and file size before it can be saved or submitted.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Author

## Secondary Actors
- CMS Validation Service  
- File Storage Service

## Trigger
Author uploads a paper manuscript file during the paper submission process.

## Success End Condition
The uploaded manuscript file is confirmed to be in an accepted format and within the allowed file size, allowing the submission process to continue.

## Failed End Condition
The uploaded manuscript file is rejected, and the system displays an error message explaining the file format or size violation.

## Preconditions
- The author is registered and logged in to the CMS.
- The author is in the paper submission process.

## Main Success Scenario
1. Author uploads a manuscript file as part of the paper submission form.
2. System checks the file format of the uploaded manuscript.
3. System checks the file size of the uploaded manuscript.
4. System confirms that the file format and size meet conference requirements.
5. System allows the author to proceed with saving or submitting the paper.

## Extensions
- **2a**: Uploaded file format is not supported  
  - **2a1**: System displays an error message listing the accepted file formats.

- **3a**: Uploaded file exceeds the maximum allowed size  
  - **3a1**: System displays an error message indicating the file size limit.

- **2b**: File upload fails due to system or network error  
  - **2b1**: System displays an error message and requests the author to retry the upload.

## Related Information
- **Priority**: High  
- **Frequency**: High  
- **Open Issues**: Accepted file formats and maximum file size limits may be updated in future CMS releases.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
An author who is logged in to the CMS is in the process of submitting a paper and uploads a manuscript file through the submission form. The system immediately evaluates the uploaded file by checking its format against the list of accepted manuscript formats and verifying that its size does not exceed the conference’s maximum limit. Once both checks pass successfully, the system confirms that the file meets all submission requirements. The author is then allowed to continue with saving the submission as a draft or completing the final paper submission.

---

### Alternative Scenario Narrative (2a: Unsupported File Format)
An author uploads a manuscript file during the submission process, but the file is in a format that is not supported by the conference (for example, an unsupported document type). When the system checks the file format, it detects the mismatch and rejects the upload. The system displays an error message listing the accepted file formats and prevents the author from proceeding until a valid file is uploaded.

---

### Alternative Scenario Narrative (3a: File Size Exceeds Limit)
An author uploads a manuscript file that uses an accepted format but exceeds the maximum allowed file size. During validation, the system detects that the file is too large and rejects the upload. An error message is displayed informing the author of the file size limit, and the author must upload a smaller file before continuing with the submission process.

---

### Alternative Scenario Narrative (2b: File Upload Failure Due to System or Network Error)
An author attempts to upload a manuscript file, but a system or network error occurs during the upload process. The file cannot be fully received or validated by the system. The system notifies the author that the upload failed due to a technical issue and prompts them to retry the upload. The submission process cannot continue until the file is successfully uploaded and validated.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario and each extension (unsupported format, oversized file, upload failure) branches from appropriate validation or upload steps, and the behavior is plausible for a file-gated submission workflow.

* Flow coverage: The scenarios cover the primary success path and the main failure conditions relevant to format, size, and upload reliability. However, the use case does not include a scenario for a corrupted/unreadable file (e.g., password-protected, malformed PDF) that passes extension checks but cannot be processed. Adding an extension for “file cannot be parsed/processed” would improve completeness.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. The upload failure scenario (2b) is a reasonable operational alternative flow consistent with the goal and does not introduce new functionality beyond handling an expected failure condition.