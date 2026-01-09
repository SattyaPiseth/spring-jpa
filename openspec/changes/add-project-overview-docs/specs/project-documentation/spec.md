## ADDED Requirements
### Requirement: Project overview document exists
The repository SHALL include a detailed project overview document under docs/PROJECT_OVERVIEW.md that explains architecture, configuration, and testing strategy.

#### Scenario: Reader wants full project context
- **WHEN** a developer opens docs/PROJECT_OVERVIEW.md
- **THEN** the document presents a structured overview covering purpose, tech stack, architecture, configuration, and testing.

### Requirement: README links to the overview
The README SHALL include a concise summary and a link to docs/PROJECT_OVERVIEW.md.

#### Scenario: Reader starts at README
- **WHEN** a developer reads README.md
- **THEN** they can find a short summary and a link to the detailed overview document.