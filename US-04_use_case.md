# UC-04: Submit Paper Manuscript

## Goal in Context
Allow an author to submit a paper manuscript so that it can be reviewed for acceptance at the conference.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Author

## Secondary Actors
- CMS Validation Service  
- CMS Database  
- File Storage Service

## Trigger
Author selects the **Submit Paper** option from their CMS dashboard.

## Success End Condition
The paper manuscript and all required metadata are successfully validated, stored in the CMS database, and the author receives confirmation of successful submission.

## Failed End Condition
The paper manuscript is not submitted, and the system displays an appropriate error message explaining the reason for failure.

## Preconditions
- The author is registered and logged in to the CMS.
- The paper submission period is open.

## Main Success Scenario
1. Author selects the **Submit Paper** option from their dashboard.
2. System displays the paper submission form.
3. Author enters required paper information, including authors, affiliations, abstract, keywords, and contact details.
4. Author uploads the paper manuscript file.
5. Author submits the paper submission form.
6. System validates that all required fields are completed.
7. System validates the manuscript file format and file size.
8. System stores the paper information and manuscript file in the CMS database.
9. System displays a success message confirming paper submission.
10. System redirects the author to their dashboard.

## Extensions
- **3a**: Author leaves one or more required fields empty  
  - **3a1**: System displays an error message indicating missing required information.

- **4a**: Uploaded file format is not supported  
  - **4a1**: System displays an error message indicating allowed file formats.

- **4b**: Uploaded file exceeds the maximum allowed size  
  - **4b1**: System displays an error message indicating the file size limit.

- **6a**: Validation of form data fails  
  - **6a1**: System highlights invalid fields and requests correction.

## Related Information
- **Priority**: High  
- **Frequency**: Medium  
- **Open Issues**: Maximum file size and supported file formats may change in future CMS versions.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
An author who is logged in to the CMS navigates to their dashboard and selects the **Submit Paper** option. The system displays the paper submission form requesting all required metadata, including author details, affiliations, abstract, keywords, and contact information. The author completes all required fields and uploads a manuscript file. After submitting the form, the system validates that all mandatory information is provided and that the uploaded file meets the required format and size constraints. Once validation succeeds, the system stores the paper metadata and manuscript file in the CMS database and file storage service. The system then displays a confirmation message indicating successful submission and redirects the author back to their dashboard.

---

### Alternative Scenario Narrative (3a: Missing Required Metadata)
An author opens the paper submission form but leaves one or more required metadata fields empty, such as the abstract or contact information. When the author submits the form, the system checks the provided information, detects missing required fields, and displays an error message indicating that all mandatory information must be completed. The paper is not submitted or stored, and the author remains on the submission form to correct the missing data.

---

### Alternative Scenario Narrative (4a: Unsupported File Format)
An author completes all required paper metadata but uploads a manuscript file in a format that is not supported by the CMS. Upon submission, the system validates the uploaded file and detects that the file format is not allowed. The system displays an error message listing the accepted file formats and prevents the submission from proceeding until a supported file is provided.

---

### Alternative Scenario Narrative (4b: File Size Exceeds Limit)
An author uploads a manuscript file that is in an accepted format but exceeds the maximum allowed file size. When the submission is processed, the system checks the file size, identifies that it exceeds the limit, and displays an error message indicating the file size restriction. The submission is rejected, and the author is prompted to upload a smaller file.

---

### Alternative Scenario Narrative (6a: Invalid Form Data)
An author submits the paper submission form with metadata that fails validation rules, such as invalid characters or improperly formatted contact details. The system validates the form data, identifies the invalid fields, and highlights them for correction. The system displays an error message requesting that the author fix the invalid information before resubmitting, and the paper is not stored until all issues are resolved.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario and each extension branch from appropriate steps in the submission process (metadata entry, file upload, and validation), and the described behavior is plausible for a conference paper submission system.

* Flow coverage: The scenarios cover the main success flow and key failure cases, including missing required metadata, unsupported file format, file size violations, and invalid form data. However, the use case does not include a scenario where file upload fails due to a system/network/storage issue. Adding an extension for upload/storage failure would improve completeness.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. All scenarios align with the stated goal, preconditions, and validation/storage steps, and no undocumented functionality is introduced.