# GLOBAL REQUIREMENTS MATRIX

Legend:
- Endpoints/Pages are taken from OpenAPI contracts when present, otherwise from `spec.md`/`tasks.md` view/controller names.
- Dependencies include hard preconditions from use-case/spec text and cross-UC lifecycle dependencies.

| UC | Key Responsibilities | Main Endpoints / Pages | Major Entities | Shared Services / Dependencies |
|---|---|---|---|---|
| UC-01 Register User | Create account, validate email/password, activate and redirect to login | `GET/POST /register`, `register.html` | User Account, Registration Submission | Email validation, password policy, DB persistence |
| UC-02 Login | Authenticate, enforce account status, redirect authorized home | `GET/POST /login`, `login.html` | User Account, Authentication Attempt | Auth service, account-status check, depends on UC-01 accounts |
| UC-03 Change Password | Verify current password, enforce new policy, force re-login | `GET/POST /account/password`, `change-password.html` | User Account, Password Change Request | Auth service, password policy, session invalidation; requires UC-02 authenticated state |
| UC-04 Submit Manuscript | Submit metadata + file, validate/store, redirect dashboard | `GET/POST /papers/submit`, `submit-paper.html` | Paper Submission, Manuscript File | Metadata validation, file storage, requires UC-02 login and open submission period |
| UC-05 Save Draft | Save in-progress paper with minimal required fields | `POST /papers/draft/save`, `submit-paper.html` save action | Paper Submission Draft | Draft validation, draft persistence; requires UC-02 login and in-progress submission context |
| UC-06 Validate File | Validate manuscript format/size/readability | `POST /papers/file/validate` | Manuscript File | File validation service; typically invoked by UC-04 flow |
| UC-07 Assign Referees | Assign referees to paper, store assignments, trigger invitations | `POST /papers/{paper_id}/referees/assign` | Referee, Referee Assignment | Reviewer lookup, workload service (UC-08), notification service (UC-09); requires editor auth |
| UC-08 Enforce Workload | Check and enforce reviewer assignment capacity | `GET /reviewers/{reviewer_id}/workload/check` | Reviewer Workload, Referee Assignment | Workload repository/service; used by UC-07 |
| UC-09 Send Invitation | Generate/send review invitation, deduplicate, failure logging | `POST /assignments/{assignment_id}/invitation/send` | Review Invitation, Delivery Failure Record | Email service, failure log, depends on UC-07 assignment |
| UC-10 Invitation Response | Accept/reject invitation, update assignment, notify editor | `POST /invitations/{invitation_id}/response` | Review Invitation, Invitation Response, Review Assignment | Response service, assignment status update, editor notify; depends on UC-09 invitation |
| UC-11 Access Review Form | Authorize referee and load review form + paper details | `GET /assignments/{assignment_id}/review-form`, `review-form` page | Review Form, Paper, Review Assignment | Authorization service; depends on UC-10 accepted invitation/assignment |
| UC-12 Submit Review | Validate/store review and notify editor | `POST /assignments/{assignment_id}/reviews` | Review, Review Form, Review Assignment | Review validation, repository, editor notification; depends on UC-11 access |
| UC-13 Final Decision | Validate all reviews complete, record accept/reject, notify author | `POST /papers/{paper_id}/decision` | Final Decision, Review, Paper, Editor, Author | Decision service, completion check, notification; depends on UC-12 review completion |
| UC-14 Receive Decision | Deliver final decision notifications and expose decision status | `POST /papers/{paper_id}/decision/notify`, `GET /papers/{paper_id}/decision` | Final Decision, Notification Delivery Failure, Paper | Notification dispatch/logging; depends on UC-13 recorded decision |
| UC-15 Generate Schedule | Build schedule from accepted papers and resources | `POST /schedule/generate` | Schedule, Session, Accepted Paper, Room, Time Slot | Scheduling algorithm, scheduling data repository; depends on accepted papers from UC-13 |
| UC-16 Edit Schedule | Edit/validate/update schedule with conflict detection | `PUT /schedule/{schedule_id}`, editable schedule page | Schedule, Session, Room, Time Slot | Conflict validator, persistence; depends on UC-15 generated schedule |
| UC-17 View Final Schedule | Read-only final published schedule with unavailable/retry handling | `GET /schedule/final`, `final-schedule.html` | Schedule, Session | Schedule retrieval service; depends on schedule publication after UC-15/16 workflow |
| UC-18 View Prices | Show registration prices by category to guests/users | `GET /registration/prices`, `registration-prices.html` | Registration Price, Attendee Category | Price repository; used by UC-19 payment selection |
| UC-19 Pay Fee | Validate/process payment, record confirmation, handle declines/outages | `POST /registration/payments`, payment pages | Payment, Registration Type, Attendee | Payment gateway adapter, payment repository; depends on UC-18 pricing and authenticated attendee |
| UC-20 Payment Ticket | Generate/store/deliver confirmation ticket and allow view/download | `POST /payments/{payment_id}/ticket`, `GET /tickets/{ticket_id}`, `ticket-status.html` | Confirmation Ticket, Payment Confirmation, Ticket Delivery Failure | Ticket service + email/in-system delivery; depends on UC-19 successful payment |

## Cross-UC Dependency Highlights

1. `UC-01 -> UC-02 -> (UC-03, UC-04, UC-05, UC-19)` for authenticated user journeys. (Source: `US-01`..`US-05`, `US-19`, corresponding `spec.md` preconditions)
2. `UC-04/05/06 -> UC-07 -> UC-08/09 -> UC-10 -> UC-11 -> UC-12 -> UC-13 -> UC-14` for submission-to-decision lifecycle. (Source: `US-04`..`US-14`; contracts and specs)
3. `UC-13 accepted papers -> UC-15 -> UC-16 -> UC-17` for scheduling lifecycle. (Source: `US-15`..`US-17`, `UC-15/16/17 spec.md`)
4. `UC-18 -> UC-19 -> UC-20` for registration commerce lifecycle. (Source: `US-18`..`US-20`; `UC-18/19/20 spec.md`)

## Conflicts / Ambiguities

- Reviewer count rules differ across SRS and UC-07 (three vs minimum-one/no-max).
- Paper file constraints differ across SRS and UC-04/06 (PDF/Word/Latex+7MB vs PDF/DOCX+20MB).
- Some tasks reference non-existent `contracts/README.md` in UC-09..UC-20.
(Sources: `CMS-SRS.pdf` §3.3-§3.4; `UC-04/06/07 spec+clarifications`; `UC-09`..`UC-20 analysis.md`)

## Open Questions

- Which authority resolves SRS-vs-UC conflicts for implementation and tests?
- Should endpoint naming and UI route naming be normalized across all UCs before coding starts?
- Should a global contract style be enforced (`Error` only vs typed validation payloads)?
