# Data Model: Make Final Paper Decision

## Entities

### Final Decision

**Purpose**: Represents the editor's accept/reject decision for a paper.

**Key Fields**:
- `decision_id`: Unique identifier
- `paper_id`: Submitted paper identifier
- `editor_id`: Editor identifier
- `decision`: accept, reject
- `decided_at`: Timestamp

### Review

**Purpose**: Represents completed review submissions used in the decision.

**Key Fields**:
- `review_id`: Unique identifier
- `paper_id`: Related paper
- `referee_id`: Reviewer identifier
- `submitted_at`: Timestamp

### Paper

**Purpose**: Represents the submitted paper under decision.

**Key Fields**:
- `paper_id`: Unique identifier
- `title`: Paper title
- `abstract`: Paper abstract

### Editor

**Purpose**: Represents the decision-making user.

**Key Fields**:
- `editor_id`: Unique identifier
- `email`: Registered email address

### Author

**Purpose**: Represents the recipient of the decision notification.

**Key Fields**:
- `author_id`: Unique identifier
- `email`: Registered email address
