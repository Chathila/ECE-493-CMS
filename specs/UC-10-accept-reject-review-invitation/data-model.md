# Data Model: Accept or Reject Review Invitation

## Entities

### Review Invitation

**Purpose**: Represents an invitation with open/expired status and related paper
and referee.

**Key Fields**:
- `invitation_id`: Unique identifier
- `status`: open, expired, responded
- `referee_id`: Assigned referee identifier
- `paper_id`: Paper identifier
- `expires_at`: Expiration timestamp

**Validation Rules**:
- Responses only allowed when `status` is open.
- Only one response per invitation.

### Invitation Response

**Purpose**: Represents the referee's accept/reject decision.

**Key Fields**:
- `response_id`: Unique identifier
- `invitation_id`: Related invitation
- `decision`: accept, reject
- `responded_at`: Timestamp

**Validation Rules**:
- One response per invitation.

### Review Assignment

**Purpose**: Represents assignment status updated after response.

**Key Fields**:
- `assignment_id`: Unique identifier
- `paper_id`: Submitted paper identifier
- `referee_id`: Assigned referee identifier
- `status`: pending, accepted, rejected

### Paper

**Purpose**: Represents the submitted paper shown to the referee.

**Key Fields**:
- `paper_id`: Unique identifier
- `title`: Paper title
- `abstract`: Paper abstract

### Referee

**Purpose**: Represents the reviewer responding to the invitation.

**Key Fields**:
- `referee_id`: Unique identifier
- `email`: Registered email address
