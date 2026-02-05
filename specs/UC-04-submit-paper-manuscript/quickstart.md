# Quickstart: Submit Paper Manuscript

## Goal

Validate the paper submission flow for UC-04.

## Preconditions

- Author is logged in.
- Submission period is open.
- File storage service is reachable.

## Happy Path

1. Open the paper submission page.
2. Enter required metadata and upload a PDF/DOCX file within 20 MB.
3. Submit the form.
4. Confirm success message is shown.
5. Confirm redirect to dashboard.

## Validation Errors

- Submit with missing required metadata and confirm error messaging.
- Submit with invalid metadata and confirm invalid fields are highlighted.
- Upload unsupported file format and confirm allowed formats error.
- Upload oversized file and confirm size limit error.

## Service Outage

- Simulate file storage unavailability and confirm upload failure message asks to
  try again.
