# CMS Project Constitution

## Core Principles

### 1) Requirements-first, traceable outputs
All generated artifacts (specifications, plans, tasks, and any implementation artifacts) must be derived strictly from:
- The CMS SRS (`CMS-SRS.pdf`)
- Use cases and acceptance tests contained in this repository

No new features, behaviors, or requirements may be invented. Every requirement must be traceable back to a use case flow and/or acceptance test.

---

### 2) Use-case fidelity
Use-case flows must be preserved exactly. Rewrites are permitted only for clarity or grammar and must not alter control flow, actors, responsibilities, or system behavior.

All functional requirements must be extracted directly from the use-case flows and remain fully congruent with them.

---

### 3) One feature per use case
Each use case is treated as an independent feature and must be specified, planned, and decomposed in its own git branch.
Branch naming MUST follow the pattern `UC-XX-<short-title>` matching the use case
number (e.g., `UC-02-user-login`).

Rework of shared artifacts across branches is allowed when required to support additional use cases, provided existing behavior is preserved.

---

### 4) No manual patching of generated artifacts
Generated artifacts must not be manually edited to “fix” errors or omissions.

If a validation fails or an artifact is incomplete, the appropriate previous step (constitution, specification, or planning) must be re-run with refined instructions to keep the project context synchronized.

---

### 5) Scope boundaries (from the CMS SRS)
The CMS supports:
- User registration and login
- Paper submission
- Reviewer assignment and reviewing
- Conference scheduling
- Conference registration and ticketing
- Public announcements

The following are explicitly out of scope:
- Accommodation facilities
- Automated publisher connection or rights delivery (manual/out of scope per SRS)

No artifacts may introduce functionality outside this defined scope.

---

### 6) Architecture and implementation constraints
The system shall be designed using an **MVC (Model–View–Controller) architecture** with **vanilla HTML, CSS, Java, and JavaScript**.

Planning and design artifacts must:
- Remain implementation-agnostic
- Avoid assumptions about specific frameworks or libraries
- Model persistence at a design level using a relational database abstraction only

All interfaces and interactions must be documented via simple, clear contracts and kept consistent across artifacts.

---

### 7) Security and reliability (high-level)
- Passwords must never be stored or transmitted in plaintext.
- Sensitive data and uploaded files must be treated as encrypted in transit and at rest (design-level requirement).
- The system must support backup and recovery expectations described in the SRS.
- The user interface must provide clear validation messages, error feedback, and confirmations.

---

## Repository Conventions

- Use cases are stored in the repository root and named `US-XX_use_case.md` (e.g., `US-01_use_case.md`).
- Acceptance tests are stored in the repository root and named `UC-XX_tests.md` (e.g., `UC-01_tests.md`).
- Generated artifacts for each use case are created in the corresponding feature branch and named:
  - `spec.md`
  - `plan.md`
  - `tasks.md`
- Each `/speckit.analyze` run must save its report as `analysis.md` in the use
  case’s feature directory.
- Each `/speckit.clarify` run must save a clarification report as
  `clarifications.md` in the use case’s feature directory, listing each question
  and the selected answer.
- Any interaction contracts or interface definitions should be placed under a `contracts/` directory when required.

---

## Governance
This constitution is the authoritative source of project constraints and principles.

Any deviation requires:
1. Updating this constitution
2. Re-synchronizing project context
3. Regenerating all affected downstream artifacts

**Version**: 1.3  
**Ratified**: 2026-02-05  
**Last Amended**: 2026-02-05  
