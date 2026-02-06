# Implementation Plan: Assign Referees to Submitted Papers

**Branch**: `UC-07-assign-referees` | **Date**: 2026-02-05 | **Spec**: /Users/chathilaratnatilake/Documents/University/WINTER 2026/ECE 493/ECE-493-CMS/specs/UC-07-assign-referees/spec.md
**Input**: Feature specification from `/specs/UC-07-assign-referees/spec.md`

## Summary

Allow editors to assign referees to submitted papers by selecting valid referee
emails. Enforce eligibility/workload limits and require at least one referee per
paper. Store assignments and send review invitations; if notifications fail, keep
assignments and show a warning.

## Technical Context

**Language/Version**: Java (backend), HTML/CSS/JavaScript (frontend, vanilla)  
**Primary Dependencies**: None (no frameworks)  
**Storage**: Relational database (unspecified)  
**Testing**: Repository acceptance tests + manual UI validation  
**Target Platform**: Web browser  
**Project Type**: web  
**Performance Goals**: Assignment confirmation within 5 seconds  
**Constraints**: MVC architecture; no framework assumptions; scope limited to SRS
features; Java backend with vanilla web front-end  
**Scale/Scope**: Conference management system with standard web scale (not
otherwise specified in SRS)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Requirements-first, traceable outputs: PASS (spec derived from US-07 and
  repository acceptance tests).
- Use-case fidelity: PASS (flows preserved; no control-flow changes).
- One feature per use case: PASS (branch `UC-07-assign-referees` dedicated to
  UC-07).
- No manual patching of generated artifacts: PASS (artifacts generated via
  workflow).
- Scope boundaries (from CMS SRS): PASS (referee assignment is in scope).
- Architecture and implementation constraints: PASS (MVC + vanilla HTML/CSS/JS,
  relational DB abstraction; Java backend).
- Security and reliability: PASS (clear validation feedback, notification
  warnings on failure).

## Project Structure

### Documentation (this feature)

```text
specs/UC-07-assign-referees/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
src/
├── controllers/
├── models/
├── services/
├── views/
└── static/

tests/
├── contract/
├── integration/
└── unit/
```

**Structure Decision**: Single web application MVC structure with controller,
model, and view separation; no frontend/backend split is required for this
feature.

## Complexity Tracking

> No constitution violations requiring justification.
