## ADDED Requirements
### Requirement: Auditing Fields
The system SHALL automatically populate auditing fields for Product entities.

#### Scenario: Create populates auditing fields
- **WHEN** a product is created
- **THEN** createdAt and updatedAt are set and non-null

#### Scenario: Update refreshes updatedAt
- **WHEN** a product is updated
- **THEN** updatedAt changes and remains non-null
