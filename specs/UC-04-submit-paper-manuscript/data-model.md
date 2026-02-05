# Data Model: Submit Paper Manuscript

## Entities

### Paper Submission

**Purpose**: Represents a submitted paper with metadata and file reference.

**Key Fields**:
- `submission_id`: Unique identifier
- `title`: Paper title
- `authors`: Author list
- `affiliations`: Author affiliations
- `abstract`: Paper abstract
- `keywords`: Keyword list
- `contact_details`: Contact name/email
- `manuscript_file_id`: Reference to stored manuscript file
- `submitted_at`: Submission timestamp

**Validation Rules**:
- Required metadata fields must be present
- Metadata must pass formatting rules
- Manuscript file must be present, in allowed format, and within size limit

### Manuscript File

**Purpose**: Represents the uploaded manuscript file and storage metadata.

**Key Fields**:
- `file_id`: Unique identifier
- `filename`: Original filename
- `format`: File format (PDF/DOCX)
- `size_bytes`: File size
- `storage_location`: Storage reference

**Validation Rules**:
- `format` must be PDF or DOCX
- `size_bytes` must be ≤ 20 MB

## Relationships

- A `Paper Submission` references one `Manuscript File`.
