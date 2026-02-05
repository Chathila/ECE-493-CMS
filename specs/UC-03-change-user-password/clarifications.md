# Clarification Report: UC-03 Change User Password

**Date**: 2026-02-05

## Questions & Answers

- Q: What happens to the current session after a password change?  
  A: User must log in again after password change.
- Q: What happens when the authentication service is unavailable?  
  A: Block change and show a try-again-later error.
- Q: How are new password security requirements defined?  
  A: External CMS password policy (unspecified here).
