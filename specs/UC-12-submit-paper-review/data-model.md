# Data Model: Submit Paper Review

## Entities

### Review

**Purpose**: Represents the completed review submitted by a referee.

**Key Fields**:
- `review_id`: Unique identifier
- `assignment_id`: Review assignment identifier
- `submitted_at`: Timestamp
- `status`: submitted

### Review Form

**Purpose**: Represents form fields and responses submitted for a paper.

**Key Fields**:
- `form_id`: Unique identifier
- `responses`: Field responses

**Validation Rules**:
- Required fields must be present and valid.

### Review Assignment

**Purpose**: Represents the referee-paper assignment.

**Key Fields**:
- `assignment_id`: Unique identifier
- `paper_id`: Submitted paper identifier
- `referee_id`: Assigned referee identifier
- `status`: accepted, submitted

### Paper

**Purpose**: Represents the submitted paper being reviewed.

**Key Fields**:
- `paper_id`: Unique identifier
- `title`: Paper title
- `abstract`: Paper abstract

### Referee

**Purpose**: Represents the reviewer submitting the review.

**Key Fields**:
- `referee_id`: Unique identifier
- `email`: Registered email address
