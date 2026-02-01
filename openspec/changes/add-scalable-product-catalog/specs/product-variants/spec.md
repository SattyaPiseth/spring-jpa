## ADDED Requirements
### Requirement: Product Variants Model
Products SHALL support zero or more variants stored in a dedicated product_variants table. Each variant SHALL reference a parent product and include sku, price, and stock fields. Variant price SHALL be required.

#### Scenario: Product without variants
- **WHEN** a Product is created without variants
- **THEN** the Product SHALL be persisted without any variant rows

#### Scenario: Variant for product
- **WHEN** a variant is created for a Product
- **THEN** the variant SHALL reference that Product
- **AND** the variant SHALL persist sku, price, and stock values

### Requirement: Effective Price Exposure
Product and variant API responses SHALL include an effectivePrice field. For products without variants, effectivePrice SHALL equal the product price. For variants, effectivePrice SHALL equal the variant price.

#### Scenario: Product effective price without variants
- **WHEN** a Product is returned without variants
- **THEN** effectivePrice SHALL equal the Product price

#### Scenario: Variant effective price
- **WHEN** a Variant is returned
- **THEN** effectivePrice SHALL equal the Variant price

### Requirement: Variant SKU Uniqueness
Variant sku values SHALL be unique across all variants.

#### Scenario: Duplicate SKU
- **WHEN** a variant is created or updated with a sku that already exists
- **THEN** the system SHALL reject the request

### Requirement: Variant Option Values
Variants SHALL support option values using typed attribute values scoped to VARIANT.

#### Scenario: Variant option values stored
- **WHEN** a variant is created or updated with option values
- **THEN** the system SHALL persist option values as typed variant attribute values
