# Spring Data JPA

## Prerequisites

- Java 21
- Docker (only required for Testcontainers integration tests)

## Quick Start

```
./gradlew clean test
```

## Project Overview

For architecture, configuration, and testing details, see:
`docs/PROJECT_OVERVIEW.md`.

## DTOs

Request/response DTOs are implemented as Java records for immutability and
to reduce accidental mutation after validation.

## Tests

### Fast integration tests (H2)

Runs the default test suite with the embedded H2 database configured in
`src/test/resources/application-test.yml` (in-memory `qa_db`).
This profile sets the H2 JDBC URL, username, password, and `ddl-auto`.

`application-test.yml`:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:qa_db;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
    username: sattya_piseth
    password: P@$5Word
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
`-Dit.tc=true`.

Testcontainers uses dynamic datasource properties in `ProductContainerIT`:
```java
@DynamicPropertySource
static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
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
- `src/integrationTest/java` runs under the `integrationTest` task (Docker).

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
