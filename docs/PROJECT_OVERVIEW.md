# Project Overview

## Purpose
- Provide a clean, layered Spring Boot CRUD API for Products.
- Demonstrate stable pagination, validation, and error handling.
- Offer a reliable testing strategy (fast H2 tests + optional Testcontainers).

## Workflow
- Codex for implementation and tests.
- Context7 for official Spring documentation references.
- OpenSpec for proposal/specs before behavior changes.
- See `docs/WORKFLOW.md` for the full process.

## Tech Stack
- Java 21
- Spring Boot (Gradle)
- Spring Web (MVC)
- Spring Data JPA + Hibernate
- MapStruct (DTO mapping)
- JUnit 5 + Spring Test
- H2 (fast tests), Testcontainers (PostgreSQL integration tests)

## Architecture (Layered)
Controller -> Service -> Repository

- Controller: HTTP concerns only (request params, status codes, DTOs).
- Service: business logic + transaction boundaries.
- Repository: persistence only (Spring Data JPA).

## Domain Model
- Product entity with auditing fields:
  - createdAt, updatedAt, createdBy, updatedBy

## DTO Boundary
- Request/response DTOs are Java records.
- Entities are not returned directly from controllers.
- Benefits: stable API contracts, safer validation, and reduced coupling.

## Mapping
- MapStruct interface mapper with `componentModel = "spring"`.
- Explicit ignore mappings for audit fields.
- Patch mapping uses `nullValuePropertyMappingStrategy = IGNORE` to preserve
  omitted fields.

## Validation
- DTOs define validation rules (e.g., name length, price > 0).
- Method parameter validation enabled for paging parameters.
- PATCH requires at least one field; null is treated as "not provided."

## Error Handling
- Central `@RestControllerAdvice`.
- Consistent error response shape:
  - timestamp, status, error, message, path
- Safe 500 responses with sanitized message; full error logged server-side.

## Pagination Stability
- `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)`
- Page JSON includes stable metadata:
  - page.size, page.number, page.totalElements, page.totalPages

## Auditing
- JPA auditing enabled.
- Audit fields populated automatically.
- Verified by integration tests.

## Configuration & Profiles
- `application.yml` holds shared defaults.
- `application-dev.yml`: `ddl-auto=create-drop`.
- `application-staging.yml` and `application-prod.yml`: `ddl-auto=validate`.
- OSIV disabled (`spring.jpa.open-in-view=false`).

## Testing Strategy
### MVC Slice Tests (`src/test/java`)
- `@WebMvcTest` for controller behavior.
- `@MockitoBean` for service mocking.
- Verify: 200/400/404/500, paging metadata, validation errors.

### Integration Tests (H2, default)
- `@SpringBootTest` + `@AutoConfigureMockMvc`.
- Uses `application-test.yml`.
- Runs with:
  - `./gradlew clean test`

### Testcontainers (PostgreSQL, opt-in)
- `src/integrationTest/java` + `integrationTest` Gradle task.
- Docker required.
- Gradle sets `spring.profiles.active=it` for integration tests; the profile lives in
  `src/integrationTest/resources/application-it.yml` to quiet noisy logs.
- Run with:
  - PowerShell: `./gradlew --% -Dit.tc=true clean integrationTest`
  - Git Bash/CMD: `./gradlew -Dit.tc=true clean integrationTest`

## Build & Gradle
- Dedicated `integrationTest` source set and task.
- Testcontainers tests are not part of default `check`.
- MapStruct configured via annotation processors.

## Git Workflow (Suggested)
- Keep changes small and focused.
- Run `./gradlew clean test` before pushing.
- Use descriptive commit messages (type:scope summary).

## Important Constraints
- No direct entity exposure in API responses.
- No secrets committed; use environment variables for real credentials.
- Test-only credentials in `application-test.yml` are placeholders for local runs.
- Keep endpoints backward compatible unless explicitly versioned.

## External Dependencies
- PostgreSQL (runtime database)
- Docker (only for Testcontainers integration tests)
