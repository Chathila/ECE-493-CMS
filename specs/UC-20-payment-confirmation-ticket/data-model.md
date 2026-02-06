# Data Model: Receive Registration Payment Confirmation Ticket

## Entities

### Confirmation Ticket

**Purpose**: Represents the registration payment confirmation ticket.

**Key Fields**:
- `ticket_id`: Unique identifier
- `attendee_id`: Attendee identifier
- `payment_id`: Related payment confirmation
- `issued_at`: Timestamp

### Payment Confirmation

**Purpose**: Represents the successful payment event.

**Key Fields**:
- `payment_id`: Unique identifier
- `amount`: Payment amount
- `confirmed_at`: Timestamp

### Attendee

**Purpose**: Represents the ticket recipient.

**Key Fields**:
- `attendee_id`: Unique identifier
- `email`: Registered email address

### Ticket Delivery Failure

**Purpose**: Represents logged delivery errors.

**Key Fields**:
- `failure_id`: Unique identifier
- `ticket_id`: Related ticket
- `channel`: email, in-system
- `failed_at`: Timestamp
- `message`: Failure description
