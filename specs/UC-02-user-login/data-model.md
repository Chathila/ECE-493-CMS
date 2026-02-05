# Data Model: User Login

## Entities

### User Account

**Purpose**: Represents a registered CMS user.

**Key Fields**:
- `user_id`: Unique identifier
- `email`: User email address (unique; used as login username)
- `password_credential`: Stored securely; never plaintext
- `status`: Active/inactive/locked
- `role`: User role for authorized access

**Validation Rules**:
- `email` must be present and valid format
- `password_credential` must satisfy CMS password policy
- `status` must allow login (active)

### Authentication Attempt

**Purpose**: Represents a login attempt and its outcome.

**Key Fields**:
- `attempt_id`: Unique identifier
- `email_input`: Email provided by user
- `password_input`: Password provided by user
- `result`: Success/failure with reason
- `attempted_at`: Timestamp

**Validation Rules**:
- Required fields must be present
- Credentials must match a registered account

## Relationships

- An `Authentication Attempt` references one `User Account` when credentials match.
