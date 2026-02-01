# Test Verification Guide

This document describes how to verify Spring Boot tests in this project, the scope of each test tier, and how these checks fit the team workflow. It also explains where the tests live, what they assert, and how to interpret common failures.

## Quick Summary

- Fast tests (H2, default): `./gradlew clean test`
- Testcontainers (PostgreSQL, opt-in): `./gradlew -Dit.tc=true clean integrationTest`

## Prerequisites

- Java 21
- Docker running (only for Testcontainers integration tests). If Docker is not reachable, Testcontainers will skip container-based tests.

## Test Tiers at a Glance

| Tier | Source set | Purpose | Command |
| --- | --- | --- | --- |
| Unit + slice | `src/test/java` | Fast feedback on controller/service/mapper behavior | `./gradlew clean test` |
| Integration (TC) | `src/integrationTest/java` | PostgreSQL container, end-to-end behavior | `./gradlew -Dit.tc=true clean integrationTest` |

## Test Locations (By Concern)

- Controller tests: `src/test/java/co/istad/springdatajpa/controller`
- Service tests: `src/test/java/co/istad/springdatajpa/service`
- Mapper tests: `src/test/java/co/istad/springdatajpa/mapper`
- Testcontainers tests: `src/integrationTest/java/co/istad/springdatajpa/integration`

## 1) Fast Tests (H2, default)

Runs the default test suite using H2 configured in `src/test/resources/application-test.yml`.

Command:

```
./gradlew clean test
```

What this covers:
- Controller slice tests (`@WebMvcTest`) for request/response validation and paging output
- Service unit tests for business logic and exception paths
- Mapper unit tests (MapStruct) for DTO summaries and mapping rules
- Spring Boot tests that rely on the H2 profile (`application-test.yml`)

Expected result:
- Build succeeds with all tests passing.

Typical assertions in this tier:
- HTTP status codes, validation errors, paging metadata
- Mapper output for DTO summaries (category/product)
- Service behavior on not-found paths and persistence calls

## 2) Integration Tests (PostgreSQL, Testcontainers)

Runs integration tests using a real PostgreSQL container.

Command (Git Bash / CMD):

```
./gradlew -Dit.tc=true clean integrationTest
```

Command (PowerShell):

```
./gradlew --% -Dit.tc=true clean integrationTest
```

What this covers:
- End-to-end HTTP + persistence behavior via Testcontainers
- DTO summaries in API responses (category/product summaries)
- Containerized PostgreSQL schema creation behavior

Expected result:
- Build succeeds and containers are started/stopped automatically.

### Docker availability troubleshooting

Testcontainers requires a working container runtime (Docker Desktop is supported, along with a few alternatives). If you see "Docker not available" or similar errors, verify Docker Desktop is running and reachable by your shell. See https://java.testcontainers.org/on_failure.html for runtime support and troubleshooting guidance.

On Windows with Docker Desktop, the integrationTest Gradle task sets `DOCKER_HOST=npipe:////./pipe/docker_engine` when not already defined.

Typical assertions in this tier:
- JSON response fields exist and match persisted data
- Paging response shape with real database queries
- Relationship mappings (category <-> product summaries)

## 3) When to run which tests

- During day-to-day development: run `./gradlew clean test`.
- Before pushing or releasing: run both `test` and `integrationTest` (if Docker is available).

## 4) Workflow Verification Checklist

Use this checklist during the Codex + Context7 + OpenSpec workflow:

1) Confirm OpenSpec state (if applicable):
   - `openspec list`
   - `openspec list --specs`
2) Verify docs are aligned with changes:
   - Update `docs/PROJECT_OVERVIEW.md` if behavior changes
   - Update `docs/WORKFLOW.md` if process changes
3) Run fast tests:
   - `./gradlew clean test`
4) Run integration tests (if Docker is available):
   - `./gradlew -Dit.tc=true clean integrationTest`
5) Record results in your report or PR notes.

## 5) Reading Failures

- Controller test failures: usually broken validation rules, changed response shapes, or paging defaults.
- Service test failures: usually repository behavior changes or missing exception paths.
- Mapper test failures: usually DTO shape changes or MapStruct mapping rules.
- Testcontainers failures: typically Docker not running, port conflicts, or schema/relationship regressions.

Test reports are here:
- `build/reports/tests/test/index.html`
- `build/reports/tests/integrationTest/index.html`

## 6) Troubleshooting

### 6.1 Gradle wrapper cannot create lock file

If you see errors about `.gradle/wrapper/dists/...zip.lck`, set a local Gradle user home:

PowerShell:

```
$env:GRADLE_USER_HOME="$pwd\.gradle-user-home"
./gradlew clean test
```

### 6.2 Spring Boot plugin not resolving

If Gradle cannot resolve `org.springframework.boot`, check:
- Network/proxy settings
- Gradle Plugin Portal connectivity

## 7) Related docs

- `docs/WORKFLOW.md`
- `docs/PROJECT_OVERVIEW.md`
