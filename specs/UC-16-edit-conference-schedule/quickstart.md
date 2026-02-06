# Quickstart: Edit Conference Schedule

## Goal

Validate schedule edits, conflict highlighting, validation errors, and save
failure handling for UC-16.

## Preconditions

- A generated schedule exists and is editable.
- Editor is logged in.

## Happy Path

1. Edit session times/rooms/assignments.
2. Submit edits and confirm schedule is saved and confirmation shown.

## Conflict Highlighting

- Create a room/time overlap and confirm conflicts are highlighted inline and
  the schedule is not saved.

## Validation Errors

- Submit with invalid or missing schedule fields and confirm field-level errors
  are shown and the schedule is not saved.

## Save Failure

- Simulate database error and confirm retry-later message is shown and changes
  are not saved.
