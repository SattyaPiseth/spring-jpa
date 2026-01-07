# Change: Add PATCH /products/{id} Partial Update

## Why
Support partial updates for Product fields while enforcing validation and consistent error handling.

## What Changes
- Add PATCH /products/{id} for partial updates of name, description, and price only.
- Require at least one updatable field; empty payload returns 400.
- Apply validation only to provided fields; omitted or null fields are ignored.
- Return 200 with updated Product DTO on success; 404 if not found; 400 on invalid payload.
- Add MVC slice tests and H2 integration tests; Testcontainers parity is opt-in.

## Impact
- Affected specs: partial-update
- Affected code: controller/service/DTOs/tests; no endpoint changes beyond adding PATCH.
