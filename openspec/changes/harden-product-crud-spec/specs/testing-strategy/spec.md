## ADDED Requirements
### Requirement: Testing Strategy Coverage
The system SHALL be verified by layered tests.

#### Scenario: MVC slice tests
- **WHEN** controller behavior is tested
- **THEN** MVC slice tests use @WebMvcTest with Boot 4 test APIs

#### Scenario: Service tests
- **WHEN** business rules are verified
- **THEN** service unit tests cover success and error paths

#### Scenario: Integration tests (embedded DB)
- **WHEN** integration tests run by default
- **THEN** they use embedded H2 and pass without Docker

#### Scenario: Integration tests (Testcontainers)
- **WHEN** Testcontainers tests are invoked with an opt-in flag
- **THEN** PostgreSQL container tests run and pass
