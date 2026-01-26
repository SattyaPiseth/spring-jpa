## Context
- Introduce Category as a first-class domain object with minimal CRUD.
- Standardize auditing across Product and Category by moving fields into a base class.

## Goals / Non-Goals
- Goals: Consistent auditing, DTO-only Category API, optional Product-category link.
- Non-Goals: Breaking Product API responses or mandatory category assignment.

## Decisions
- Use a mapped superclass for auditing fields to ensure uniform columns.
- Category table name is `categories` with UUID primary key.
- Product -> Category association is optional to preserve existing behavior.

## Risks / Trade-offs
- Optional relationship avoids breaking changes but allows uncategorized products.
- Base auditing class requires careful mapping to avoid duplicate columns.

## Migration Plan
- Rely on existing schema management (ddl-auto) for dev/test.
- Staging/prod use validate; schema migration is out of scope for this proposal.

## Open Questions
- None; requirements confirmed by stakeholder.