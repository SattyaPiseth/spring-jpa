## ADDED Requirements
### Requirement: Product Partial Update (PATCH)
The system SHALL support PATCH /products/{id} for partial updates of name, description, and price only.

#### Scenario: Patch success
- **WHEN** at least one updatable field is provided and valid
- **THEN** return 200 with updated ProductResponse

#### Scenario: Patch with empty body
- **WHEN** no updatable fields are provided
- **THEN** return 400 Bad Request

#### Scenario: Patch with invalid field
- **WHEN** a provided field violates validation
- **THEN** return 400 Bad Request with ErrorResponse

#### Scenario: Patch not found
- **WHEN** a non-existent product id is patched
- **THEN** return 404 Not Found with ErrorResponse

#### Scenario: Patch ignores null or absent fields
- **WHEN** fields are omitted or null
- **THEN** existing values remain unchanged

### Requirement: Patch Validation Rules
The system SHALL validate only fields provided in the patch payload.

#### Scenario: Name validation
- **WHEN** name is provided
- **THEN** it MUST be non-blank and within length constraints

#### Scenario: Description validation
- **WHEN** description is provided
- **THEN** it MUST satisfy length constraints

#### Scenario: Price validation
- **WHEN** price is provided
- **THEN** it MUST be greater than 0.00
