# Workflow: Codex + Context7 + OpenSpec

This project uses three tools together to keep changes consistent and auditable.

## Roles

- OpenSpec: proposal/specs first for new features or behavior changes.
- Context7: official Spring docs lookup; facts must be recorded in the context file.
- Codex: implementation, tests, and minimal diffs after approval.

## When To Use OpenSpec

Use OpenSpec for:
- New endpoints or behavior changes
- Schema or data model changes
- Architectural changes
- Breaking changes or new capabilities

Skip OpenSpec for:
- Bug fixes that restore intended behavior
- Typos, formatting, comments
- Non-breaking dependency updates
- Configuration-only tweaks

## Required Inputs

Before coding:
- Read `openspec/project.md`
- Run `openspec list` and `openspec list --specs`
- Create a change proposal and validate it

## Docs Policy (Context7)

- Use only official sources: `docs.spring.io`, `spring.io`, `github.com/spring-projects`
- Update `docs/SPRING_BOOT_OFFICIAL_CONTEXT.md` when guidance is missing or outdated
- Do not assume facts that are not explicitly stated in official docs

## Implementation Flow

1) Proposal and specs (OpenSpec) -> approval
2) Docs validation (Context7) -> update context file if needed
3) Implementation (Codex) -> tests -> report diffs

## Agent Procedure (Required)

1) Read `openspec/project.md`
2) Run `openspec list` and `openspec list --specs`
3) If change requires OpenSpec:
   - Create proposal + spec deltas
   - Run `openspec validate <change-id> --strict`
   - Wait for approval before coding
4) If Context7 facts are needed:
   - Query official sources
   - Update `docs/SPRING_BOOT_OFFICIAL_CONTEXT.md`
5) Implement with minimal diffs
6) Run tests (`./gradlew clean test`, `./gradlew --% -Dit.tc=true clean integrationTest` when needed)
7) Update docs if behavior or structure changes (e.g., DTO package layout)
8) Report changes and test results

## Verification

Typical commands:
- `./gradlew clean test`
- `./gradlew --% -Dit.tc=true clean integrationTest` (Testcontainers opt-in, Docker required)
