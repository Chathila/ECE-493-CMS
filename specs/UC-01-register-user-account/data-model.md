# Data Model: Register User Account

## Entities

### User Account

**Purpose**: Represents a registered CMS user.

**Key Fields**:
- `user_id`: Unique identifier
- `email`: User email address (unique; used as login username)
- `password_credential`: Stored securely; never plaintext
- `status`: Active/inactive
- `created_at`: Account creation timestamp

**Validation Rules**:
- `email` must be present, valid format, and unique
- `password_credential` must satisfy CMS password policy

**State Transitions**:
- `inactive` -> `active` on successful registration

### Registration Submission

**Purpose**: Represents a registration attempt and its validation outcomes.

**Key Fields**:
- `submission_id`: Unique identifier
- `email_input`: Email provided by user
- `password_input`: Password provided by user
- `validation_result`: Pass/fail with reason
- `submitted_at`: Submission timestamp

**Validation Rules**:
- Required fields must be present
- Email format must be valid
- Email must be unique
- Password must meet CMS policy

## Relationships

- A `Registration Submission` may create one `User Account` if validation passes.
- A `User Account` is created from exactly one successful `Registration Submission`.
