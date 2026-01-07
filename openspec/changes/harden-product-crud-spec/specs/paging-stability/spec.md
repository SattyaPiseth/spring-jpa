## ADDED Requirements
### Requirement: Paging and Sorting Stability
The system SHALL provide stable paging metadata and deterministic sorting behavior.

#### Scenario: Page metadata fields present
- **WHEN** a paged list is requested
- **THEN** page.size, page.number, page.totalElements, and page.totalPages are present

#### Scenario: Sorting is deterministic
- **WHEN** a sort field and direction are requested
- **THEN** the returned content is ordered according to the sort

#### Scenario: Out-of-range page is stable
- **WHEN** a page number beyond the last page is requested
- **THEN** return a stable page response with empty content and consistent metadata

#### Scenario: Pageable propagation is verified
- **WHEN** page, size, and sort are provided
- **THEN** the service receives a Pageable that matches the requested parameters
