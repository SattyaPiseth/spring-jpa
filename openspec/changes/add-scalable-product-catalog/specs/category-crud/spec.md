## ADDED Requirements
### Requirement: Category Hierarchy Fields
Category DTOs SHALL include optional parentId and sortOrder fields. Create and update Category requests SHALL accept optional parentId and sortOrder values.

#### Scenario: Create category without parent
- **WHEN** a category is created without parentId
- **THEN** the category SHALL be persisted as a root category with parentId = null

#### Scenario: Create category with parent
- **WHEN** a category is created with a valid parentId
- **THEN** the category SHALL be persisted as a child of that parent

#### Scenario: Update category parent
- **WHEN** a category is updated with a new parentId
- **THEN** the category SHALL be re-parented accordingly

#### Scenario: Sort order optional
- **WHEN** a category is created or updated without sortOrder
- **THEN** the category SHALL be persisted with sortOrder = null

### Requirement: Category Hierarchy Validation
If parentId is provided, it SHALL reference an existing category and SHALL NOT reference the category itself. If sortOrder is provided, it SHALL be a non-negative integer.

#### Scenario: Parent does not exist
- **WHEN** a category request provides a parentId that does not exist
- **THEN** the system SHALL return 400 Bad Request with the standard error response shape

#### Scenario: Parent is self
- **WHEN** a category request provides parentId equal to the category id
- **THEN** the system SHALL return 400 Bad Request with the standard error response shape

#### Scenario: Invalid sortOrder
- **WHEN** a category request provides a negative sortOrder
- **THEN** the system SHALL return 400 Bad Request with the standard error response shape
