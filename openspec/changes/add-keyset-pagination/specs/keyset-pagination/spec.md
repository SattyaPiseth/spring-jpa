## ADDED Requirements
### Requirement: Keyset Cursor Format
Cursor tokens SHALL be opaque strings encoding the last (createdAt, id) pair from the previous page. The system SHALL reject malformed or unparseable cursors with 400 Bad Request and the standard error response shape.

#### Scenario: Cursor encodes last seen row
- **WHEN** a keyset response is generated
- **THEN** nextCursor SHALL encode the last item’s createdAt and id values

#### Scenario: Invalid cursor
- **WHEN** a request includes an invalid cursor
- **THEN** the system SHALL return 400 Bad Request with the standard error response shape

### Requirement: Keyset Response Shape
Keyset pagination responses SHALL return an object containing items, nextCursor, and hasNext. nextCursor SHALL be null when there is no next page.

#### Scenario: Keyset response with more results
- **WHEN** there are more results after the current page
- **THEN** the response SHALL include items and a non-null nextCursor
- **AND** hasNext SHALL be true

#### Scenario: Keyset response at end
- **WHEN** no further results remain
- **THEN** nextCursor SHALL be null
- **AND** hasNext SHALL be false

### Requirement: Product Variant Keyset Listing
GET /products/{id}/variants SHALL support keyset pagination when cursor is provided and SHALL use createdAt DESC, id DESC sorting.

#### Scenario: List variants with cursor
- **WHEN** GET /products/{id}/variants is requested with cursor
- **THEN** the system SHALL return a keyset response sorted by createdAt DESC, id DESC
