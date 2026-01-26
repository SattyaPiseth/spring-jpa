# Change: Bidirectional Product/Category association

## Why
We need bidirectional navigation between Product and Category while keeping JSON APIs safe and avoiding lazy-loading surprises.

## What Changes
- Add bidirectional mapping between Product and Category.
- Add helper methods to keep both sides in sync.
- Ensure JSON serialization avoids infinite recursion.

## Impact
- Affected specs: product-category
- Affected code: entities, mappers/DTOs (as needed), tests
