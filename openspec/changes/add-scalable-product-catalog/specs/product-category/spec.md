## MODIFIED Requirements
### Requirement: Optional Product Category Association
Products SHALL support an optional primary category and optional additional category associations. The primary category SHALL be exposed via existing categoryId and category fields to preserve response shapes.

#### Scenario: Product without any category
- **WHEN** a Product is created without a category
- **THEN** the Product SHALL be persisted with a null primary category
- **AND** the Product SHALL have no product-category link rows

#### Scenario: Product with primary category
- **WHEN** a Product is created or updated with a valid primary category
- **THEN** the Product SHALL persist primary_category_id
- **AND** the Product SHALL include that category in product-category links

#### Scenario: Product with additional categories
- **WHEN** a Product is associated to additional categories
- **THEN** the Product SHALL have product-category links for each associated category
- **AND** the primary category SHALL be one of those links

#### Scenario: Backward-compatible response shape
- **WHEN** a Product is retrieved
- **THEN** the response categoryId and category fields SHALL represent the primary category

#### Scenario: Legacy category_id retained during deprecation
- **WHEN** a Product is created or updated with a primary category during the deprecation period
- **THEN** the legacy category_id column SHALL be kept in sync with the primary category
