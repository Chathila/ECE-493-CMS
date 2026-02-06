# Data Model: Access Paper Review Form

## Entities

### Review Form

**Purpose**: Represents the form structure required to evaluate a paper.

**Key Fields**:
- `form_id`: Unique identifier
- `version`: Form version

### Paper

**Purpose**: Represents the submitted paper displayed with the review form.

**Key Fields**:
- `paper_id`: Unique identifier
- `title`: Paper title
- `abstract`: Paper abstract

### Referee

**Purpose**: Represents the reviewer attempting to access the form.

**Key Fields**:
- `referee_id`: Unique identifier
- `email`: Registered email address

### Review Assignment

**Purpose**: Represents the referee-paper assignment used for authorization.

**Key Fields**:
- `assignment_id`: Unique identifier
- `paper_id`: Submitted paper identifier
- `referee_id`: Assigned referee identifier
- `status`: assigned, accepted, rejected

**Validation Rules**:
- Access allowed only when assignment status is accepted.
