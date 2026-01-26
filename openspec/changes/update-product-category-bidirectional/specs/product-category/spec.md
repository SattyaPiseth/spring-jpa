# product-category Delta Specification

## ADDED Requirements
### Requirement: Bidirectional Product-Category Navigation
Categories SHALL expose their related Products, and Products SHALL reference a Category, with both sides kept consistent in memory.

#### Scenario: Add product to category
- **WHEN** a Product is associated to a Category
- **THEN** the Category SHALL include the Product in its products collection
- **AND** the Product SHALL reference that Category

#### Scenario: Remove product from category
- **WHEN** a Product is disassociated from a Category
- **THEN** the Category SHALL not include the Product
- **AND** the Product SHALL reference no Category

### Requirement: JSON-Safe Serialization
The API SHALL avoid infinite recursion and lazy-loading errors when serializing Product/Category relationships.

#### Scenario: Serialize product with category
- **WHEN** a Product response includes its Category
- **THEN** serialization SHALL not recurse through Category.products

#### Scenario: Serialize category with products
- **WHEN** a Category response includes Products
- **THEN** serialization SHALL not recurse through Product.category
