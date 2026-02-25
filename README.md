# ECE-493-CMS

Initial Java (no-framework) CMS skeleton implementing **UC-01 Register User Account** with an MVC-ish structure.

## Implemented UC-01 scope

- Homepage provides a registration option (`/` -> `/register`)
- Registration form collects email + password
- Validation for required fields and email format
- Duplicate email detection
- Password policy validation
- Account creation with immediate active status
- Password hashing (PBKDF2) and no plaintext persistence
- Success redirect to login with confirmation message
- Email validation service outage returns try-again-later error

## Run the app

```bash
mvn clean compile
java -cp target/classes cms.app.CmsApplication 8080
```

Open: `http://localhost:8080`

## Run tests

```bash
mvn clean test
```

## Run coverage (with branch checks)

```bash
mvn clean verify
```

Coverage outputs:

- HTML report: `target/site/jacoco/index.html`
- XML report: `target/site/jacoco/jacoco.xml`

JaCoCo enforces branch coverage checks for UC-01 core classes (`cms.services.*`, `cms.controllers.*`) during `verify`.
