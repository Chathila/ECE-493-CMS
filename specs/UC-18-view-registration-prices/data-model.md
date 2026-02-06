# Data Model: View Conference Registration Prices

## Entities

### Registration Price

**Purpose**: Represents pricing for a specific attendee category.

**Key Fields**:
- `price_id`: Unique identifier
- `category`: Attendee category
- `amount`: Price amount

### Attendee Category

**Purpose**: Represents pricing categories (e.g., regular, student, author).

**Key Fields**:
- `category_id`: Unique identifier
- `name`: Category name
