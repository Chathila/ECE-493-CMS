# Data Model: Validate Paper File Format and Size

## Entities

### Manuscript File

**Purpose**: Represents the uploaded file and validation metadata.

**Key Fields**:
- `file_id`: Unique identifier
- `filename`: Original filename
- `format`: File format (PDF/DOCX)
- `size_bytes`: File size
- `validation_status`: Valid/invalid with reason

**Validation Rules**:
- `format` must be PDF or DOCX
- `size_bytes` must be ≤ 20 MB
- File must be readable/parsable
