# Data Model: Pay Conference Registration Fee

## Entities

### Payment

**Purpose**: Represents the payment transaction and confirmation.

**Key Fields**:
- `payment_id`: Unique identifier
- `attendee_id`: Attendee identifier
- `registration_type`: Selected registration category
- `amount`: Payment amount
- `status`: pending, confirmed, declined
- `confirmed_at`: Timestamp

### Registration Type

**Purpose**: Represents the selected registration category.

**Key Fields**:
- `type_id`: Unique identifier
- `name`: Category name
- `price`: Fee amount

### Attendee

**Purpose**: Represents the user paying the fee.

**Key Fields**:
- `attendee_id`: Unique identifier
- `email`: Registered email address
