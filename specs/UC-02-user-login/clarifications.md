# Clarification Report: UC-02 User Login

**Date**: 2026-02-05

## Questions & Answers

- Q: What identifier is used for login?  
  A: Email is the username for login.
- Q: What happens when the authentication service is unavailable?  
  A: Block login and show a try-again-later error.
- Q: How should repeated failed login attempts be handled?  
  A: No lockout or throttling in this feature.
