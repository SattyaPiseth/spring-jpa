# Change: Add scalable product catalog foundations

## Why
The current product/category model supports only a single optional category and lacks variants and typed attributes, which limits long-term e-commerce growth and filtering.

## What Changes
- Add unlimited-depth category hierarchy with optional sibling sort order.
- Introduce many-to-many product-category links while keeping a primary category for SEO/breadcrumb defaults.
- Add product variants (SKU, price, stock, option values) under a parent product.
- Add a typed attribute system for products and variants (string/number/boolean) to enable future filtering.
- Preserve existing endpoints and response shapes; keep legacy product.category_id during a deprecation period.
- Add migration/backfill steps for new tables/columns.

## Impact
- Affected specs: category-crud, product-category, product-filtering
- New specs: product-variants, catalog-attributes
- Affected code: entities, DTOs/mappers, repositories, services, migrations, seed data, tests
