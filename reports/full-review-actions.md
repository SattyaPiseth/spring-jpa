# Full Review Action Checklist

## Top Fixes (Priority Order)
1. Add authentication/authorization (Spring Security) and protect write endpoints.
2. Remove N+1 risk in product listing by using fetch joins or `@EntityGraph` for category summaries.
3. Add a DB index on `products.category_id` to speed category filters.

## Critical
- [ ] None

## High
- [ ] Add Spring Security and enforce authz on controllers.

## Medium
- [ ] Fetch categories efficiently for product list endpoints.
- [ ] Add index on `products.category_id` (migration or JPA index).

## Low
- [ ] Wire `AuditorAware` to real principals (after auth is in place).
- [ ] Clarify required default profile env vars or add non-prod defaults.

## Follow-ups
- [ ] Rerun `./gradlew clean test` with `GRADLE_USER_HOME` set if the wrapper lock error persists.
- [ ] Decide whether to add CI (GitHub Actions) and an app Dockerfile for deployments.
