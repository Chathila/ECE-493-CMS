# Data Model: Receive Review Invitation Email

## Entities

### Review Invitation

**Purpose**: Represents an invitation message tied to a specific referee-paper
assignment.

**Key Fields**:
- `invitation_id`: Unique identifier
- `assignment_id`: Referee-paper assignment identifier
- `referee_id`: Assigned referee identifier
- `paper_id`: Paper identifier
- `status`: pending, sent, failed
- `sent_at`: Timestamp when sent

**Validation Rules**:
- Only one invitation per `assignment_id`.
- `status` transitions: pending → sent or pending → failed.

### Delivery Failure Record

**Purpose**: Captures failed invitation attempts for audit and follow-up.

**Key Fields**:
- `failure_id`: Unique identifier
- `invitation_id`: Related invitation
- `reason`: Failure reason (e.g., invalid email, service outage)
- `failed_at`: Timestamp

### Referee

**Purpose**: Represents the reviewer receiving invitations.

**Key Fields**:
- `referee_id`: Unique identifier
- `email`: Registered email address

**Validation Rules**:
- Email must be valid at send time; invalid email triggers editor notification.

### Paper

**Purpose**: Represents the submitted paper included in the invitation.

**Key Fields**:
- `paper_id`: Unique identifier
- `title`: Paper title
- `abstract`: Paper abstract

### Referee Assignment

**Purpose**: Represents assignment of a referee to a paper.

**Key Fields**:
- `assignment_id`: Unique identifier
- `paper_id`: Submitted paper identifier
- `referee_id`: Assigned referee identifier
- `assigned_at`: Timestamp

**Validation Rules**:
- Duplicate assignments for the same referee-paper pair do not create new
  invitations.
