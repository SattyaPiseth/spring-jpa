## 1. Planning & Validation
- [ ] 1.1 Confirm DTO field additions for hierarchy, categories, variants, and attributes
- [ ] 1.2 Validate OpenSpec change definition (openspec validate --strict)

## 2. Persistence Model
- [ ] 2.1 Add category hierarchy columns (parent_id, sort_order)
- [ ] 2.2 Add primary_category_id to products and create product_categories join table
- [ ] 2.3 Add product_variants and attribute tables with constraints and indexes

## 3. Domain & Mapping
- [ ] 3.1 Update entities and helper methods for many-to-many and primary category sync
- [ ] 3.2 Add variant entity and attribute entities with typed values
- [ ] 3.3 Update mappers/DTOs to include new fields (parentId, sortOrder, effectivePrice) without breaking existing shapes

## 4. Services & Validation
- [ ] 4.1 Enforce parent category integrity and sortOrder validation
- [ ] 4.2 Enforce primary category consistency with product_categories
- [ ] 4.3 Enforce variant SKU uniqueness and typed attribute validation

## 5. API & Filtering
- [ ] 5.1 Keep existing endpoints and response shapes stable
- [ ] 5.2 Update product list filtering to use product_categories while honoring categoryId

## 6. Data Migration & Seed
- [ ] 6.1 Backfill product_categories and primary_category_id from legacy category_id
- [ ] 6.2 Update data initialization to set primary category and categories links

## 7. Tests
- [ ] 7.1 Add tests for category hierarchy and sort order
- [ ] 7.2 Add tests for many-to-many category links and primary category
- [ ] 7.3 Add tests for variants and typed attributes
- [ ] 7.4 Add tests for categoryId filtering with many-to-many
