# Data Model: Receive Final Paper Decision

## Entities

### Final Decision

**Purpose**: Represents the recorded accept/reject decision for a paper.

**Key Fields**:
- `decision_id`: Unique identifier
- `paper_id`: Related paper
- `decision`: accept, reject
- `recorded_at`: Timestamp

### Author

**Purpose**: Represents the recipient of the decision notification.

**Key Fields**:
- `author_id`: Unique identifier
- `email`: Registered email address

### Editor

**Purpose**: Represents the user notified of delivery failures.

**Key Fields**:
- `editor_id`: Unique identifier
- `email`: Registered email address

### Notification Delivery Failure

**Purpose**: Represents logged delivery errors.

**Key Fields**:
- `failure_id`: Unique identifier
- `decision_id`: Related decision
- `channel`: email, in-system
- `failed_at`: Timestamp
- `message`: Failure description

### Paper

**Purpose**: Represents the submitted paper with decision status.

**Key Fields**:
- `paper_id`: Unique identifier
- `title`: Paper title
- `status`: accept, reject
