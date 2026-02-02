# Spring Data JPA

## Prerequisites

- Java 21
- Docker (only required for Testcontainers integration tests; tests are skipped if Docker is unavailable)

## Quick Start

```bash
./gradlew clean test
```

## Developer Shortcuts

Commands:
```bash
./gradlew clean test
./gradlew --% -Dit.tc=true clean integrationTest
./gradlew bootRun
```

Scripts (Linux/macOS):
```bash
./scripts/dev.sh
./scripts/test.sh
./scripts/it.sh
```

Windows note: use the Gradle commands above (PowerShell supports `--%`).

## Local H2 (file-based)

Run the app with a persistent H2 database on disk:

```bash
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

## Architecture Overview

- Controller → Service → Repository → Database
- DTO-only API responses with validation at the boundary
- JPA auditing on entities (createdAt/updatedAt)

## Package Map (src/main/java/co/istad/springdatajpa)

- `controller/` REST endpoints
- `service/` business logic interfaces
- `service/impl/` service implementations
- `repository/` Spring Data JPA repositories
- `entity/` JPA entities (domain model)
- `dto/request/` request DTOs
- `dto/response/` response DTOs
- `mapper/` MapStruct mappers
- `initialize/` seed and backfill runners
- `util/` small utilities (cursor encoding)
- `config/`, `error/`, `exception/` infrastructure

## Key Entities and Relationships

- Category hierarchy: Category has optional parent and children (unlimited depth), optional sortOrder.
- Product ↔ Category: many-to-many associations plus a primary category (legacy category_id kept in sync).
- Product variants: Product has many ProductVariant entries (sku, price, stock).
- Typed attributes: AttributeDefinition with data type and scope; ProductAttributeValue and VariantAttributeValue store typed values.

## Key Design Decisions

- Typed attributes (STRING/NUMBER/BOOLEAN) instead of JSON blobs for filtering reliability.
- Category hierarchy via adjacency list (parent_id).
- Primary category preserved for SEO/breadcrumb defaults while supporting many-to-many.
- Keyset pagination for large lists using createdAt DESC, id DESC ordering.

## Where to Look (high-signal classes)

- Controllers: `CategoryController`, `ProductController`, `VariantController`
- Services: `CategoryService`, `ProductService`
- Repositories: `CategoryRepository`, `ProductRepository`, `ProductVariantRepository`
- Mappers: `CategoryMapper`, `ProductMapper`
- Initialization: `DataInitialization`, `CatalogBackfill`
- Keyset cursor: `KeysetCursor`

## Local Environment

Local test credentials can be set in `.env.file` (ignored by git). See
`.env.example` for the expected keys, including `TEST_DB_USER` and
`TEST_DB_PASSWORD`. Do not commit `.env.file`.

Default profile requires PostgreSQL env vars:
`POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`.
If those are not set, use the `local` profile for H2 or provide a `.env.file`.

Seed data flags (dev/local enabled, staging/prod disabled):
- `app.seed.enabled`
- `app.seed.attributes.enabled`
- `app.seed.variants.enabled`
- `app.seed.categoryHierarchy.enabled`

## DTOs

Request/response DTOs are implemented as Java records for immutability and
to reduce accidental mutation after validation.

## Pagination: Offset vs Keyset

Offset paging (`page`/`size`):
- Easy random access (page N)
- Slower on deep pages; can skip/duplicate when data changes

Keyset paging (`cursor`):
- Fast at scale, stable under inserts/deletes
- No jump-to-page; requires cursor from previous page

In this project:
- Use keyset for large lists or infinite scroll
- Use offset for admin/jump-to-page

## Keyset Pagination (Implementation Details)

Exact ordering:
- `createdAt DESC, id DESC` for products and variants
- Implemented in repository queries (keyset first/next page)

Cursor format:
- Base64 URL-safe (no padding)
- Encodes the string: `<createdAt>|<id>` where `createdAt` is ISO-8601 from `Instant`
- The cursor is opaque and must not be parsed by clients.

Keyset response shape:
```json
{
  "items": [ ... ],
  "nextCursor": "opaque-token-or-null",
  "hasNext": true
}
```

Keyset usage:
- Provide `cursor` query param to use keyset.
- First page uses an empty cursor value: `cursor=`

Indexes supporting keyset:
- `products` index on `created_at, id`
- `product_variants` index on `product_id, created_at, id`

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

### Product Variants
- `POST /products/{id}/variants` create variant for product
- `GET /products/{id}/variants` list variants for product (paged or keyset)
- `PUT /products/{id}/variants/{variantId}` update variant for product
- `GET /variants/{id}` get variant by id

### Attributes
- `POST /products/{id}/attributes` create product attribute
- `GET /products/{id}/attributes` list product attributes
- `PUT /products/{id}/attributes/{attributeId}` update product attribute
- `POST /variants/{id}/attributes` create variant attribute
- `GET /variants/{id}/attributes` list variant attributes
- `PUT /variants/{id}/attributes/{attributeId}` update variant attribute

## How to Test Offset vs Keyset Power

Deep-page latency:
1) Offset: call `GET /products?page=0&size=20`, then `page=1000`, then `page=10000`.
2) Keyset: call `GET /products?size=20&cursor=` and advance using `nextCursor`.
3) Compare response times and DB logs.

Correctness under change:
1) Fetch page 1 (offset or keyset).
2) Insert a new product between requests.
3) Fetch page 2 and verify duplicates/missing items:
   - Offset may skip or duplicate
   - Keyset should not

Postgres EXPLAIN (ANALYZE, BUFFERS):
- Enable SQL logging (`logging.level.org.hibernate.SQL=DEBUG`)
- Capture the actual query from logs
- Run:
```bash
EXPLAIN (ANALYZE, BUFFERS)
<actual query here>;
```

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

```bash
./gradlew clean test
```

### PostgreSQL integration tests (Testcontainers)

Runs the Testcontainers suite in `src/integrationTest/java` using a real
PostgreSQL container. Docker must be running. The `integrationTest` task runs
container-based tests. The `-Dit.tc=true` flag controls Testcontainers usage
inside the suite.

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
```bash
./gradlew --% -Dit.tc=true clean integrationTest
```

Git Bash / CMD:
```bash
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

```bash
./gradlew clean test
./gradlew --% -Dit.tc=true clean integrationTest
```
