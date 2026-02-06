# Data Model: View Final Conference Schedule

## Entities

### Schedule

**Purpose**: Represents the published conference schedule.

**Key Fields**:
- `schedule_id`: Unique identifier
- `published_at`: Timestamp
- `status`: published

### Session

**Purpose**: Represents scheduled sessions within the conference.

**Key Fields**:
- `session_id`: Unique identifier
- `schedule_id`: Related schedule
- `time_slot`: Session time
- `location`: Session location
- `title`: Session title
