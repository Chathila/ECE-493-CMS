# Data Model: Assign Referees to Submitted Papers

## Entities

### Referee

**Purpose**: Represents a reviewer eligible for assignment.

**Key Fields**:
- `referee_id`: Unique identifier
- `email`: Referee email address
- `workload_count`: Number of assigned papers
- `max_workload`: Maximum allowed assignments

### Referee Assignment

**Purpose**: Represents a referee assigned to a paper.

**Key Fields**:
- `assignment_id`: Unique identifier
- `paper_id`: Submitted paper identifier
- `referee_id`: Assigned referee
- `assigned_at`: Timestamp

**Validation Rules**:
- Referee email must exist
- Referee workload must be within limit
- At least one referee must be assigned
