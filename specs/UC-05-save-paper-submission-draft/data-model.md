# Data Model: Save Paper Submission Draft

## Entities

### Paper Submission Draft

**Purpose**: Represents an in-progress submission with partial metadata.

**Key Fields**:
- `draft_id`: Unique identifier
- `title`: Paper title
- `authors`: Author list (optional for draft)
- `affiliations`: Author affiliations (optional for draft)
- `abstract`: Paper abstract (optional for draft)
- `keywords`: Keyword list (optional for draft)
- `contact_details`: Corresponding author email
- `updated_at`: Last saved timestamp

**Validation Rules**:
- `title` and `contact_details` must be present for draft save
- Other fields may be empty during draft save

## Relationships

- A `Paper Submission Draft` belongs to one author.
