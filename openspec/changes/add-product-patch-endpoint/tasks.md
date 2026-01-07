## 1. Specification Alignment
- [x] 1.1 Define partial update requirements and scenarios

## 2. Implementation
- [x] 2.1 Add PATCH endpoint and request DTO for partial updates
- [x] 2.2 Apply field-level validation only when provided
- [x] 2.3 Update service layer to support partial updates

## 3. Tests
- [x] 3.1 MVC slice tests for PATCH success/invalid/empty/not-found
- [x] 3.2 H2 integration tests for PATCH auditing and persistence
- [x] 3.3 Optional Testcontainers parity tests

## 4. Verification
- [x] 4.1 Run ./gradlew clean test
- [x] 4.2 Run ./gradlew --% -Dit.tc=true clean integrationTest
