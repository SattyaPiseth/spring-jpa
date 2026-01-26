# audit-base Specification

## Purpose
TBD - created by archiving change add-category-audit-base. Update Purpose after archive.
## Requirements
### Requirement: Shared Auditing Base Entity
All auditable domain entities SHALL inherit auditing fields (createdAt, updatedAt, createdBy, updatedBy) from a shared base class.

#### Scenario: Audited entity saves and updates
- **WHEN** an auditable entity is created and updated
- **THEN** createdAt and updatedAt SHALL be populated
- **AND** createdBy and updatedBy SHALL be populated by the auditor provider

