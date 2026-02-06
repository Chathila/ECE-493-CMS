# Data Model: Edit Conference Schedule

## Entities

### Schedule

**Purpose**: Represents the conference schedule to edit.

**Key Fields**:
- `schedule_id`: Unique identifier
- `updated_at`: Timestamp
- `status`: draft, final

### Session

**Purpose**: Represents a scheduled session within the conference.

**Key Fields**:
- `session_id`: Unique identifier
- `schedule_id`: Related schedule
- `room_id`: Assigned room
- `time_slot_id`: Assigned time slot

### Room

**Purpose**: Represents an assigned room.

**Key Fields**:
- `room_id`: Unique identifier
- `name`: Room name

### Time Slot

**Purpose**: Represents an assigned time slot.

**Key Fields**:
- `time_slot_id`: Unique identifier
- `start_time`: Start time
- `end_time`: End time

### Editor

**Purpose**: Represents the user editing the schedule.

**Key Fields**:
- `editor_id`: Unique identifier
- `email`: Registered email address
