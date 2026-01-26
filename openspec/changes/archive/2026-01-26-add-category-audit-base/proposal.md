# Change: Add Category Domain and Shared Auditing Base

## Why
- The domain needs a Category entity with minimal CRUD to support Product classification.
- Auditing fields are duplicated across entities and should be centralized for consistency.

## What Changes
- Add a Category domain entity (UUID PK, table name `categories`).
- Introduce an audited base entity to share Spring Data JPA auditing fields.
- Add minimal Category CRUD API using DTOs.
- Add an optional Product -> Category association without changing existing Product behavior.

## Impact
- Affected specs: category-crud, audit-base, product-category
- Affected code: entity layer, service layer, repository layer, controller layer, DTOs, mapper, tests
- Data model: new `categories` table; Product gains optional FK to Category