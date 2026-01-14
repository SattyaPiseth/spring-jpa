## ADDED Requirements
### Requirement: Product List Filter by Category
The system SHALL allow GET /products to be filtered by an optional categoryId parameter while preserving paging and sorting behavior.

#### Scenario: List products without category filter
- **WHEN** GET /products is requested without categoryId
- **THEN** the system SHALL return the full pageable product list

#### Scenario: List products with category filter
- **WHEN** GET /products is requested with a valid categoryId
- **THEN** the system SHALL return only products associated to that category in a pageable response

### Requirement: Category Filter Not Found
When categoryId is provided and does not exist, the system SHALL return 404 Not Found with the standard error response shape.

#### Scenario: Category filter not found
- **WHEN** GET /products is requested with a categoryId that does not exist
- **THEN** the system SHALL return 404 Not Found with the standard error response shape