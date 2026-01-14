# Project Conventions

## Overview
- Spring Boot 4 application using layered architecture (controller -> service -> repository).
- DTO-only API responses and request validation at the boundary.
- Spring Data JPA auditing enabled.
- Stable paging JSON via Spring Data Web support.

## Standards
- Prefer minimal, explicit changes.
- Keep endpoints backward compatible unless explicitly approved.
- Avoid exposing JPA entities in API responses.
- Use UUID as primary keys for domain entities.

## Testing
- MVC slice tests for controller behavior.
- Integration tests with embedded H2 by default.
- Optional Testcontainers tests gated by system property.