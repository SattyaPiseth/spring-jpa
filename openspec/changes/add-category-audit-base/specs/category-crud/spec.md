## ADDED Requirements
### Requirement: Category CRUD API
The system SHALL provide a Category API that supports create, read, list, update, and delete operations using DTOs only.

#### Scenario: Create category
- **WHEN** a valid category request is submitted
- **THEN** the system SHALL return 201 with the created Category DTO

#### Scenario: List categories
- **WHEN** categories are requested
- **THEN** the system SHALL return a pageable list of Category DTOs

#### Scenario: Update category
- **WHEN** a valid update is submitted for an existing category
- **THEN** the system SHALL return 200 with the updated Category DTO

#### Scenario: Delete category
- **WHEN** an existing category is deleted
- **THEN** the system SHALL return 204

### Requirement: Category Validation
Category name SHALL be required and limited to 255 characters. Category description SHALL be optional and limited to 2000 characters.

#### Scenario: Invalid category
- **WHEN** a category request violates validation constraints
- **THEN** the system SHALL return 400 with the standard error response shape

### Requirement: Category PATCH Description
The system SHALL provide PATCH /categories/{id} to support partial updates of Category.description using DTOs only.
PATCH requests SHALL include at least one updatable field. For this capability, the only updatable field is description; therefore description SHALL be provided in the request body.

#### Scenario: Patch category description
- **WHEN** a PATCH request is submitted to /categories/{id} with a valid description value
- **THEN** the system SHALL return 200 OK with the updated Category DTO

#### Scenario: Patch request with empty body
- **WHEN** a PATCH request is submitted to /categories/{id} with an empty JSON body ({}) or with no updatable fields
- **THEN** the system SHALL return 400 Bad Request with the standard error response shape

### Requirement: Category PATCH Validation
When description is provided in a Category PATCH request, the system SHALL validate that description length is <= 2000 characters.

#### Scenario: Patch description too long
- **WHEN** a PATCH request provides description longer than 2000 characters
- **THEN** the system SHALL return 400 Bad Request with the standard error response shape

### Requirement: Category PATCH Not Found
The system SHALL return 404 Not Found when patching a Category that does not exist.

#### Scenario: Patch missing category
- **WHEN** a PATCH request is submitted to /categories/{id} for a Category that does not exist
- **THEN** the system SHALL return 404 Not Found with the standard error response shape

### Requirement: Category PATCH Auditing
A successful Category PATCH operation SHALL update the Category auditing fields (e.g., last modified timestamp and modifier) as managed by Spring Data JPA auditing.

#### Scenario: Patch updates auditing
- **WHEN** a Category PATCH request successfully updates description
- **THEN** the system SHALL update the Category “last modified” auditing fields
- **AND** the system SHALL NOT modify the original “created” auditing fields

### Requirement: Category PATCH Response Shape
The system SHALL return the updated Category DTO in the PATCH response and SHALL NOT expose JPA entities.

#### Scenario: Patch response uses DTO only
- **WHEN** a Category PATCH request succeeds
- **THEN** the response body SHALL contain only the Category DTO fields
