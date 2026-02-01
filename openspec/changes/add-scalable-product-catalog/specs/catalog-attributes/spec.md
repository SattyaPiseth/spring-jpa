## ADDED Requirements
### Requirement: Attribute Definitions
The system SHALL store attribute definitions with a name, data type (STRING|NUMBER|BOOLEAN), scope (PRODUCT|VARIANT|BOTH), and filterable flag.

#### Scenario: Create attribute definition
- **WHEN** an attribute definition is created with name, data type, scope, and filterable
- **THEN** the system SHALL persist the definition for later use

### Requirement: Typed Attribute Values
The system SHALL persist attribute values for products and variants using type-specific fields. Exactly one of value_string, value_number, or value_boolean SHALL be set per attribute value according to the definition data type.

#### Scenario: Product attribute value stored
- **WHEN** a product attribute value is provided with a matching data type
- **THEN** the system SHALL persist the value in the correct typed field

#### Scenario: Variant attribute value stored
- **WHEN** a variant attribute value is provided with a matching data type
- **THEN** the system SHALL persist the value in the correct typed field

#### Scenario: Attribute value type mismatch
- **WHEN** an attribute value is provided that does not match the definition data type
- **THEN** the system SHALL reject the request

### Requirement: Attribute Scope Enforcement
Attribute definitions scoped to PRODUCT SHALL be used only for product attribute values, and definitions scoped to VARIANT SHALL be used only for variant attribute values.

#### Scenario: Product-only attribute used on variant
- **WHEN** a variant attribute value references a PRODUCT-scoped attribute definition
- **THEN** the system SHALL reject the request
