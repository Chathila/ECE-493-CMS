# GLOBAL SYSTEM SUMMARY

## Scope and Modules

CMS is a web-based conference management system that covers user onboarding/authentication, paper submission and review, decision/notification workflows, scheduling, and conference registration/payment, with announcements publicly visible. (Source: `CMS-SRS.pdf` §1.2, §2.1, §3.1-§3.7; `US-01`..`US-20`; `specs/UC-01-*`..`specs/UC-20-*` `spec.md`)

### 1) Authentication and Account Management
- Registration with email + password, uniqueness and policy checks, immediate account activation, redirect to login. (Source: `specs/UC-01-register-user-account/spec.md` FR-001..FR-014; `US-01_use_case.md`)
- Login with email-as-username and account status enforcement (inactive/locked denial). (Source: `specs/UC-02-user-login/spec.md` FR-001..FR-011; `US-02_use_case.md`)
- Password change for authenticated users, current password verification, forced re-login after success. (Source: `specs/UC-03-change-user-password/spec.md` FR-001..FR-014; `US-03_use_case.md`)

### 2) Submission and File Handling
- Full manuscript submission with metadata and file upload, plus draft save at any stage. (Source: `specs/UC-04-submit-paper-manuscript/spec.md`; `specs/UC-05-save-paper-submission-draft/spec.md`; `US-04_use_case.md`; `US-05_use_case.md`)
- File validation is a separable capability with explicit format/size/corruption handling. (Source: `specs/UC-06-validate-paper-file/spec.md` FR-001..FR-007)

### 3) Review Assignment and Reviewing
- Editor assigns referees, workload checks enforced, invitations delivered, invitation response recorded, review form access gated by authorization, completed reviews submitted to editor. (Source: `specs/UC-07-*`..`specs/UC-12-*` `spec.md`; `US-07_use_case.md`..`US-12_use_case.md`; `CMS-SRS.pdf` §3.4-§3.5)

### 4) Final Decision and Notification
- Editor records final accept/reject decision after review completion.
- Decision notification sent via email + in-system; delivery failures logged; decision status visible in CMS immediately. (Source: `specs/UC-13-*` and `specs/UC-14-*` `spec.md`; `US-13_use_case.md`; `US-14_use_case.md`)

### 5) Scheduling
- System generates schedule from accepted papers + rooms/time slots.
- Editor can edit/resolve conflicts; final schedule is published/viewed with unavailable/retry handling. (Source: `specs/UC-15-*`..`specs/UC-17-*` `spec.md`; `US-15_use_case.md`..`US-17_use_case.md`; `CMS-SRS.pdf` §3.6)

### 6) Registration, Payment, and Ticketing
- Prices viewable to guests/registered users.
- Attendee pays registration fee via online payment service.
- Successful payment triggers ticket generation/storage/delivery with pre-delivery CMS access. (Source: `specs/UC-18-*`..`specs/UC-20-*` `spec.md`; `US-18_use_case.md`..`US-20_use_case.md`; `CMS-SRS.pdf` §3.7 FR12)

## Cross-Cutting Constraints

- Architecture: object-oriented, three-layer architecture / MVC style separation is consistently required across UC plans. (Source: `CMS-SRS.pdf` §2.4; all `specs/UC-*/plan.md` Technical Context + structure)
- Tech stack: Java backend, vanilla HTML/CSS/JavaScript frontend, relational DB (SQL in SRS), browser-based app (Chrome/Firefox target in SRS). (Source: `CMS-SRS.pdf` §2.1-§2.4; all `plan.md`)
- Security:
  - Passwords must never be stored/transmitted plaintext.
  - User/paper data must be encrypted over network and persisted securely.
  - Backup/recovery expectation exists in SRS.
  (Source: `specs/UC-01/02/03/spec.md` FR-012/FR-010/FR-012; `CMS-SRS.pdf` §4 Security Requirements)
- Validation/error handling:
  - Each UC defines explicit validation gates and user-facing errors.
  - Multiple UCs mandate retry-later guidance on service/DB failure.
  - 09-20 clarifications tighten message content rules (often minimal content).
  (Source: all `specs/UC-*/spec.md`; all `clarifications.md`)
- Data persistence:
  - Persistent state is relational DB-centric for users, papers, reviews, decisions, schedules, prices, payments, tickets.
  - File storage service is used for manuscript files.
  (Source: all `data-model.md`; `UC-04` plan/spec; `CMS-SRS.pdf` §3.x)

## Shared Conceptual Entities (Cross-UC)

- User / Account: `user_id`, `email`, credential, status, role. (Source: `UC-01/02/03 data-model.md`)
- Author / Referee / Editor / Attendee role views over users, each with role-specific flows and notifications. (Source: `CMS-SRS.pdf` §2.2; `UC-07`..`UC-20 data-model.md`)
- Paper and Submission: `paper_id`/`submission_id`, metadata (title/authors/affiliations/abstract/keywords/contact), manuscript reference. (Source: `UC-04/05 data-model.md`; `CMS-SRS.pdf` §3.3)
- Manuscript File: format, size, storage location, validation status. (Source: `UC-04/06 data-model.md`)
- Assignment + Invitation + Response: assignment status lifecycle from assigned/invited to accepted/rejected/submitted. (Source: `UC-07`..`UC-12 data-model.md`)
- Review + Review Form: reviewer responses, required fields, submission status. (Source: `UC-11/12 data-model.md`; `CMS-SRS.pdf` §3.5)
- Final Decision + Decision Notification: accept/reject record plus delivery state/logging. (Source: `UC-13/14 data-model.md`)
- Schedule + Session + Room + Time Slot: generated and editable conference plan with publication state. (Source: `UC-15/16/17 data-model.md`; `CMS-SRS.pdf` §3.6)
- Registration Price + Category + Registration Type: price presentation and payment basis. (Source: `UC-18/19 data-model.md`; `CMS-SRS.pdf` §3.7)
- Payment + Confirmation Ticket + Delivery Failure log. (Source: `UC-19/20 data-model.md`; `CMS-SRS.pdf` FR12)

## Conflicts / Ambiguities

1. Referee count rule conflict:
- SRS expects three reviewers per paper and checks against exceeding three. (Source: `CMS-SRS.pdf` §3.4, FR7)
- UC-07 clarification says minimum one, no maximum. (Source: `specs/UC-07-assign-referees/spec.md` FR-005/FR-005a; `clarifications.md`)

2. Manuscript formats and size conflict:
- SRS says PDF/Word/Latex with max 7MB. (Source: `CMS-SRS.pdf` §3.3)
- UC-04 and UC-06 lock to PDF/DOCX and 20MB. (Source: `UC-04 clarifications/spec`; `UC-06 clarifications/spec`)

3. Error-message specificity is repeatedly underspecified:
- Analysis files call out vague “clear error message” requirements for several UCs. (Source: `specs/UC-01`..`UC-08/analysis.md`)

4. Contract artifact mismatch in tasks:
- Multiple tasks reference `contracts/README.md` that does not exist. (Source: `specs/UC-09`..`UC-20/analysis.md`)

## Open Questions (No invented answers)

- Which source is authoritative where SRS and UC clarifications diverge (notably reviewer count, manuscript format/size)? (Source conflict above)
- What is the concrete CMS password policy definition used by UC-01/03 validations? (Source: `UC-01` and `UC-03` clarifications/spec)
- Are lockout/throttling and rate limiting intentionally excluded for production, or deferred only for these UCs? (Source: `UC-01/02 clarifications`)
- Is there a standard error response schema beyond `{ message }` and occasional `fields[]`/`missing[]` for all endpoints? (Source: contracts across UCs)
- What is the canonical publication boundary between UC-15 admin-only generated schedule and UC-17 publicly visible final schedule? (Source: `UC-15 spec` FR-010 vs `UC-17 spec` FR-005)
