# Quickstart: Validate Paper File Format and Size

## Goal

Validate file format/size checks for UC-06.

## Preconditions

- Author is logged in and in the submission process.

## Happy Path

1. Upload a PDF or DOCX file within 20 MB.
2. Confirm validation success message.
3. Confirm author can proceed.

## Validation Errors

- Upload unsupported format and confirm allowed formats error.
- Upload oversized file and confirm size limit error.
- Upload corrupted/unreadable file and confirm cannot-be-processed error.

## Upload Failure

- Simulate upload failure and confirm retry message.
