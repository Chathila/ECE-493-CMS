# Clarifications: UC-09 Receive Review Invitation Email

**Created**: 2026-02-06

## Questions and Answers

1. **Q**: What is the retry behavior for failed invitation sends?
   **A**: No automatic retries; log failure and notify editor only.
2. **Q**: How should the system handle an invalid referee email address?
   **A**: Notify the editor to update the referee’s email.
3. **Q**: How should duplicate assignments for the same referee-paper pair be handled?
   **A**: Suppress duplicate invitations for the same pair.
