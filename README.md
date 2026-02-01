# Spring Data JPA

## Prerequisites

- Java 21
- Docker (only required for Testcontainers integration tests; tests are skipped if Docker is unavailable)

## Quick Start

```
./gradlew clean test
```

## Local H2 (file-based)

Run the app with a persistent H2 database on disk:

```
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

H2 Console:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/qa_db;MODE=PostgreSQL;DB_CLOSE_DELAY=-1`
- User: `${TEST_DB_USER:sa}`
- Password: `${TEST_DB_PASSWORD:}`

## Project Overview

For architecture, configuration, and testing details, see:
`docs/PROJECT_OVERVIEW.md`.

For workflow rules (Codex + Context7 + OpenSpec) and filesystem MCP usage, see:
`docs/WORKFLOW.md`.

## Local Environment

Local test credentials can be set in `.env.file` (ignored by git). See
`.env.example` for the expected keys, including `TEST_DB_USER` and
`TEST_DB_PASSWORD`. Do not commit `.env.file`.

Default profile requires PostgreSQL env vars:
`POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`.
If those are not set, use the `local` profile for H2 or provide a `.env.file`.

Seed data for local/dev can be toggled with `app.seed.enabled=true|false`
in `application-local.yml` or `application-dev.yml`.

## DTOs

Request/response DTOs are implemented as Java records for immutability and
to reduce accidental mutation after validation.

## Minimal API Docs

### Categories
- `GET /categories` list categories (paged)
- `GET /categories/{id}` get category by id
- `POST /categories` create category
- `PUT /categories/{id}` update category
- `PATCH /categories/{id}` patch category description (and hierarchy fields)
- `DELETE /categories/{id}` delete category

### Products
- `GET /products` list products (paged, optional `categoryId`)
- `GET /products/{id}` get product by id
- `POST /products` create product
- `PUT /products/{id}` update product
- `PATCH /products/{id}` patch product
- `DELETE /products/{id}` delete product

### Keyset Pagination (Recommended)
Offset paging (`page`/`size`) remains supported for backward compatibility. For large datasets, use `cursor` to enable keyset pagination.

Ordering guarantees:
- Keyset pages are ordered by `createdAt DESC, id DESC`.
- The cursor encodes the last `(createdAt, id)` from the previous page.

Products keyset example (first page uses empty cursor):
```
GET /products?size=20&cursor=
```

Response:
```json
{
  "items": [ ... ],
  "nextCursor": "opaque-token-or-null",
  "hasNext": true
}
```

Variants keyset example (first page uses empty cursor):
```
GET /products/{id}/variants?size=20&cursor=
```

### Product Variants
- `POST /products/{id}/variants` create variant for product
- `GET /products/{id}/variants` list variants for product (paged)
- `PUT /products/{id}/variants/{variantId}` update variant for product
- `GET /variants/{id}` get variant by id

### Attributes
- `POST /products/{id}/attributes` create product attribute
- `GET /products/{id}/attributes` list product attributes
- `PUT /products/{id}/attributes/{attributeId}` update product attribute
- `POST /variants/{id}/attributes` create variant attribute
- `GET /variants/{id}/attributes` list variant attributes
- `PUT /variants/{id}/attributes/{attributeId}` update variant attribute

## Best Practices Applied

- Layered architecture (Controller → Service → Repository)
- DTO-only API responses (no entity exposure)
- Centralized error handling (`@RestControllerAdvice`)
- Validation at the boundary with Bean Validation
- Stable paging output via Spring Data Web support
- JPA auditing for created/updated timestamps
- Bidirectional mapping with helper methods for consistency
- JSON-safe serialization using summary DTOs
- OSIV disabled for clearer transactional boundaries
- Fast H2 tests + optional Testcontainers integration tests

## Tests

### Fast integration tests (H2)

Runs the default test suite with the embedded H2 database configured in
`src/test/resources/application-test.yml` (in-memory `qa_db`).
This profile sets the H2 JDBC URL, username, password, and `ddl-auto`.
The username/password can be supplied via `.env.file` or environment variables.
See `.env.example` for local defaults.

`application-test.yml`:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:qa_db;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
    username: ${TEST_DB_USER:sa}
    password: ${TEST_DB_PASSWORD:}
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
```

```
./gradlew clean test
```

### PostgreSQL integration tests (Testcontainers)

Runs the Testcontainers suite in `src/integrationTest/java` using a real
PostgreSQL container. Docker must be running. Tests are gated by
`-Dit.tc=true` (the `integrationTest` task defaults this to `true`).
If Docker is not reachable, Testcontainers will skip container-based tests.

Testcontainers uses dynamic datasource properties in `ProductContainerIT`:
```java
@DynamicPropertySource
static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
}
```

PowerShell:
```
./gradlew --% -Dit.tc=true clean integrationTest
```

Git Bash / CMD:
```
./gradlew -Dit.tc=true clean integrationTest
```

## Gradle Test Configuration

The build separates fast tests and container-based tests:

- `src/test/java` runs under the default `test` task (fast, H2).
- `src/integrationTest/java` runs under the `integrationTest` task (Docker, opt-in).

Key build setup (from `build.gradle`):

- `sourceSets.integrationTest` defines the dedicated integration test source set.
- `integrationTestImplementation` extends `testImplementation`.
- `integrationTestRuntimeOnly` extends `testRuntimeOnly`.
- `integrationTest` task uses `sourceSets.integrationTest.runtimeClasspath`
  and runs only when explicitly invoked.

```gradle
sourceSets {
    integrationTest {
        java.srcDir file('src/integrationTest/java')
        resources.srcDir file('src/integrationTest/resources')
        compileClasspath += sourceSets.main.output
        runtimeClasspath += sourceSets.main.output
    }
}

configurations.named('integrationTestImplementation') {
    extendsFrom configurations.testImplementation
}

configurations.named('integrationTestRuntimeOnly') {
    extendsFrom configurations.testRuntimeOnly
}

tasks.register('integrationTest', Test) {
    description = 'Runs Testcontainers integration tests.'
    group = 'verification'
    testClassesDirs = sourceSets.integrationTest.output.classesDirs
    classpath = sourceSets.integrationTest.runtimeClasspath
    systemProperty 'it.tc', System.getProperty('it.tc', 'true')
    systemProperty 'spring.profiles.active', 'it'
    shouldRunAfter tasks.named('test')
    useJUnitPlatform()
}
```

Commands:

```
./gradlew clean test
```

```
./gradlew --% -Dit.tc=true clean integrationTest
```
