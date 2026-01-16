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

## Verification

Typical commands:
- `./gradlew clean test`
- `./gradlew --% -Dit.tc=true clean integrationTest`
