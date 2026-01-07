## 1. Audit and Spec Alignment
- [x] 1.1 Review existing controllers/services/repos/DTOs against CRUD contract requirements
- [x] 1.2 Verify paging/sorting stability behavior and test coverage
- [x] 1.3 Verify error response contract and safe 500 handling
- [x] 1.4 Verify auditing configuration and add integration test coverage if missing

## 2. Test Coverage Enhancements
- [x] 2.1 Add MVC slice tests for CRUD endpoints and status codes as needed
- [x] 2.2 Add paging/sort stability tests (including out-of-range behavior)
- [x] 2.3 Add error contract tests for 400/404/500 (safe 500 assertions)
- [x] 2.4 Add service-level unit tests for business rules and exception semantics
- [x] 2.5 Add integration tests (H2 default, Testcontainers opt-in)

## 3. Configuration and Review Outputs
- [x] 3.1 Confirm OSIV and profile ddl-auto settings
- [x] 3.2 Document scoring rubric and generate review score output
- [x] 3.3 Run verification: ./gradlew clean test; ./gradlew --% -Dit.tc=true clean integrationTest
