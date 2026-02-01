## Context
The current data model supports a single optional category per product and no variants or attributes. The goal is to keep existing APIs stable while introducing a scalable catalog architecture.

## Goals / Non-Goals
- Goals:
  - Support unlimited-depth category hierarchies with optional sibling ordering.
  - Support many-to-many product-category associations with a primary category.
  - Support product variants with SKU, price, stock, and option values.
  - Support typed attributes on products and variants.
  - Preserve existing endpoints and response shapes.
  - Provide a safe migration/backfill plan.
- Non-Goals:
  - Reworking existing endpoint routes or pagination formats.
  - Implementing advanced search (full-text, faceting) in this change.

## Decisions
- Decision: Use adjacency list for category hierarchy (parent_id) with optional sort_order.
  - Rationale: Minimal schema change, supports unlimited depth, simple queries.
- Decision: Keep products.primary_category_id and retain products.category_id during deprecation.
  - Rationale: Backward compatibility for existing DTOs and filtering.
- Decision: Represent variants in a dedicated product_variants table linked to products.
  - Rationale: Clear separation between base product and sellable variants.
- Decision: Implement typed attributes via attribute definitions and typed value tables for products and variants.
  - Rationale: Supports filtering and avoids untyped JSON fields.

## Updated Schema (logical)

### categories
- id (UUID, PK)
- name (string, unique)
- description (string, nullable)
- parent_id (UUID, FK -> categories.id, nullable)
- sort_order (int, nullable)
- auditing fields

Indexes:
- idx_categories_parent_id
- idx_categories_parent_id_sort_order (optional for ordered siblings)

### products
- id (UUID, PK)
- name, description, price
- category_id (UUID, FK -> categories.id, nullable) **legacy**
- primary_category_id (UUID, FK -> categories.id, nullable) **new**
- auditing fields

Indexes:
- idx_products_primary_category_id

### product_categories
- product_id (UUID, FK -> products.id)
- category_id (UUID, FK -> categories.id)
- PRIMARY KEY (product_id, category_id)

Indexes:
- idx_product_categories_category_id

### product_variants
- id (UUID, PK)
- product_id (UUID, FK -> products.id)
- sku (string, unique)
- price (decimal)
- stock (int)
- auditing fields

Indexes:
- idx_product_variants_product_id
- uq_product_variants_sku

### attribute_definitions
- id (UUID, PK)
- name (string)
- data_type (enum: STRING|NUMBER|BOOLEAN)
- scope (enum: PRODUCT|VARIANT|BOTH)
- filterable (boolean)

Indexes:
- uq_attribute_definitions_name_scope

### product_attribute_values
- product_id (UUID, FK -> products.id)
- attribute_id (UUID, FK -> attribute_definitions.id)
- value_string (string, nullable)
- value_number (decimal, nullable)
- value_boolean (boolean, nullable)
- PRIMARY KEY (product_id, attribute_id)

### variant_attribute_values
- variant_id (UUID, FK -> product_variants.id)
- attribute_id (UUID, FK -> attribute_definitions.id)
- value_string (string, nullable)
- value_number (decimal, nullable)
- value_boolean (boolean, nullable)
- PRIMARY KEY (variant_id, attribute_id)

## Relationships
- Category parent-child: categories.parent_id -> categories.id
- Product primary category: products.primary_category_id -> categories.id
- Product-category many-to-many: product_categories
- Product-variant: product_variants.product_id -> products.id
- Attributes: attribute_definitions -> product_attribute_values / variant_attribute_values

## Migration Plan
1. Add new columns to categories and products (parent_id, sort_order, primary_category_id).
2. Create product_categories, product_variants, attribute_definitions, product_attribute_values, variant_attribute_values tables.
3. Backfill product_categories from products.category_id where not null.
4. Backfill products.primary_category_id from products.category_id where not null.
5. Keep products.category_id as legacy for a deprecation period; write paths set both primary_category_id and category_id.
6. Add safety checks to prevent invalid parent references and ensure primary category is included in product_categories.
7. Update seed data to populate new structures.

## Risks / Trade-offs
- Adjacency list does not provide fast subtree queries without recursive SQL; acceptable for now.
- Maintaining legacy category_id and primary_category_id requires consistency guarantees.

## Open Questions
None.
