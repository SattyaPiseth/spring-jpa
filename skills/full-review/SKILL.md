---
name: full-review
description: End-to-end review of a Spring Boot repository with prioritized, actionable findings. Use when asked to run a full review, end-to-end review, security+JPA performance review, or to generate reports like reports/full-review.md and reports/full-review-actions.md.
---

# Full Review (Spring Boot)

Use this skill to perform a complete repo review with a clear output contract and generate structured reports.

## Workflow

1) Detect project type and entry points
- Identify build system (Gradle/Maven), Spring Boot entry class, modules.
- Prefer `rg --files` and `rg` for discovery.

2) Read key files
- `README.md`, build files (`build.gradle`, `pom.xml`), configs (`application.yml`, `application.properties`, profile variants), `docker-compose.yml`, `Dockerfile`, CI files if present.
- Scan `src/` for controllers/services/repos and JPA entities.

3) Run safe checks (if feasible)
- Try `./gradlew test` or `./gradlew check` (or Maven equivalent) only if available.
- If sandbox blocks execution, note it in the report and skip.

4) Static scan for issues
- Config risks: hardcoded secrets, default credentials, weak auth config, missing profiles, unsafe actuator exposure.
- Dependency hygiene: outdated/unused, duplicate versions, risky starters.
- JPA risks: N+1 patterns, missing `@Transactional`, lazy loading pitfalls, indexing assumptions.
- Logging/exception handling: swallowed exceptions, noisy logs, missing context.
- Tests: missing coverage for controllers/services/repos; flaky patterns.
- CI/Docker readiness: missing build steps, env handling.

5) Categorize findings
- Critical / High / Medium / Low.
- Each finding must include: title, severity, evidence (file path + snippet summary), impact, recommendation.

6) Generate outputs
- Main report: `reports/full-review.md`
- Actions checklist: `reports/full-review-actions.md`
- Optional diff: `patches/full-review.patch` only if user explicitly asks for patches.

## Output Format

Use the templates in `templates/`:
- `templates/report_full_review.md`
- `templates/actions_checklist.md`

If generating a patch, follow `patch_rules.md` and keep diffs minimal and safe.

## Notes

- Prefer concise, actionable findings over long narratives.
- Always include concrete file paths and exact suggestions.
- If a check cannot be performed, state why and provide a safe alternative.
