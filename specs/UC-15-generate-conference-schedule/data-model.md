# Data Model: Generate Conference Schedule

## Entities

### Schedule

**Purpose**: Represents the generated conference schedule.

**Key Fields**:
- `schedule_id`: Unique identifier
- `generated_at`: Timestamp
- `generated_by`: Administrator identifier

### Session

**Purpose**: Represents scheduled sessions within the conference.

**Key Fields**:
- `session_id`: Unique identifier
- `schedule_id`: Related schedule
- `room_id`: Assigned room
- `time_slot_id`: Assigned time slot

### Accepted Paper

**Purpose**: Represents papers accepted for presentation.

**Key Fields**:
- `paper_id`: Unique identifier
- `title`: Paper title

### Room

**Purpose**: Represents available rooms for sessions.

**Key Fields**:
- `room_id`: Unique identifier
- `name`: Room name

### Time Slot

**Purpose**: Represents available time slots for sessions.

**Key Fields**:
- `time_slot_id`: Unique identifier
- `start_time`: Start time
- `end_time`: End time
