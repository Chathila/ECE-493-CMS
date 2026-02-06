# Data Model: Enforce Reviewer Workload Limit

## Entities

### Reviewer Workload

**Purpose**: Represents the number of papers assigned to a reviewer.

**Key Fields**:
- `reviewer_id`: Unique identifier
- `assigned_count`: Current assignment count
- `workload_limit`: Configured maximum (default 5)

**Validation Rules**:
- Assignment allowed only if `assigned_count` < `workload_limit`

### Referee Assignment

**Purpose**: Represents a reviewer assigned to a paper.

**Key Fields**:
- `assignment_id`: Unique identifier
- `paper_id`: Submitted paper identifier
- `reviewer_id`: Assigned reviewer
- `assigned_at`: Timestamp
