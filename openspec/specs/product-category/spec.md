# product-category Specification

## Purpose
TBD - created by archiving change add-category-audit-base. Update Purpose after archive.
## Requirements
### Requirement: Optional Product Category Association
Products SHALL be able to reference a Category optionally, without requiring a category for existing Product behavior.

#### Scenario: Product without category
- **WHEN** a Product is created without a category
- **THEN** the Product SHALL be persisted with a null category reference

#### Scenario: Product with category
- **WHEN** a Product is created or updated with a valid category reference
- **THEN** the Product SHALL be associated to that Category

