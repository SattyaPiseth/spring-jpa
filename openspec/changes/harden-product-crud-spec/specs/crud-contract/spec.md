## ADDED Requirements
### Requirement: Product CRUD Contract
The system SHALL expose Product CRUD endpoints with consistent DTO-only responses and status codes.

#### Scenario: Create product success
- **WHEN** a valid ProductCreateRequest is submitted
- **THEN** return 201 with a ProductResponse body

#### Scenario: Read product by id success
- **WHEN** an existing product id is requested
- **THEN** return 200 with a ProductResponse body

#### Scenario: Read product by id not found
- **WHEN** a non-existent product id is requested
- **THEN** return 404 with a standard ErrorResponse body

#### Scenario: List products paged
- **WHEN** products are listed with paging parameters
- **THEN** return 200 with a stable page response and ProductResponse content

#### Scenario: Update product success
- **WHEN** a valid ProductUpdateRequest is submitted for an existing product
- **THEN** return 200 with a ProductResponse body

#### Scenario: Delete product success
- **WHEN** an existing product id is deleted
- **THEN** return 204 with an empty body
