# Clarification Report: UC-01 Register User Account

**Date**: 2026-02-05

## Questions & Answers

- Q: What is the account activation flow after registration?  
  A: Account is active immediately after registration; user is redirected to login.
- Q: How are password security requirements defined?  
  A: Policy is external/unspecified; system validates “meets CMS policy”.
- Q: What happens if the email validation service is unavailable?  
  A: Block registration and show a try-again-later error.
- Q: What identifier is used for login?  
  A: Email is the username for login.
- Q: Should registration attempts be rate-limited?  
  A: No rate limiting in this feature.
