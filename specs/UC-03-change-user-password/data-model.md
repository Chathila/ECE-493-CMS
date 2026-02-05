# Data Model: Change User Password

## Entities

### User Account

**Purpose**: Represents a registered CMS user.

**Key Fields**:
- `user_id`: Unique identifier
- `email`: User email address
- `password_credential`: Stored securely; never plaintext
- `status`: Active/inactive/locked

**Validation Rules**:
- `password_credential` must satisfy CMS password policy

### Password Change Request

**Purpose**: Represents a password change attempt and its outcome.

**Key Fields**:
- `request_id`: Unique identifier
- `current_password_input`: Current password provided by user
- `new_password_input`: New password provided by user
- `confirmation_input`: Confirmation password provided by user
- `result`: Success/failure with reason
- `requested_at`: Timestamp

**Validation Rules**:
- Required fields must be present
- Current password must match stored credentials
- New password must meet CMS policy
- New password and confirmation must match

## Relationships

- A `Password Change Request` targets one `User Account`.
