## MODIFIED Requirements
### Requirement: Product List Filter by Category
The system SHALL allow GET /products to be filtered by an optional categoryId parameter while preserving paging and sorting behavior. Offset paging (page/size) SHALL remain supported. When a cursor parameter is provided, the system SHALL use keyset pagination with a stable sort of createdAt DESC, id DESC.

#### Scenario: List products without category filter (offset)
- **WHEN** GET /products is requested without categoryId and without cursor
- **THEN** the system SHALL return the full pageable product list using offset paging

#### Scenario: List products with category filter (offset)
- **WHEN** GET /products is requested with a valid categoryId and without cursor
- **THEN** the system SHALL return only products associated to that category in a pageable response

#### Scenario: List products with cursor (keyset)
- **WHEN** GET /products is requested with cursor
- **THEN** the system SHALL return a keyset response sorted by createdAt DESC, id DESC

### Requirement: Category Filter Not Found
When categoryId is provided and does not exist, the system SHALL return 404 Not Found with the standard error response shape.

#### Scenario: Category filter not found
- **WHEN** GET /products is requested with a categoryId that does not exist
- **THEN** the system SHALL return 404 Not Found with the standard error response shape
