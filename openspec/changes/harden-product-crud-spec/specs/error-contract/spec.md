## ADDED Requirements
### Requirement: Error Response Contract
The system SHALL return a consistent ErrorResponse shape for 400, 404, and 500 errors.

#### Scenario: Validation failure returns 400
- **WHEN** an invalid request body is submitted
- **THEN** return 400 with ErrorResponse and error details

#### Scenario: Resource not found returns 404
- **WHEN** a requested product does not exist
- **THEN** return 404 with ErrorResponse

#### Scenario: Unexpected error returns safe 500
- **WHEN** an unhandled exception occurs
- **THEN** return 500 with a sanitized message and no internal details

### Requirement: Safe 500 Response Content
The system SHALL ensure 500 responses do not leak internal details.

#### Scenario: 500 response body contains no internal markers
- **WHEN** an unexpected error is triggered
- **THEN** the full response body does not contain Exception, StackTrace, package names, or SQL keywords
