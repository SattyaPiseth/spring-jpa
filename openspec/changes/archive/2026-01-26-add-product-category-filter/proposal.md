# Change: Add Product Filtering by Category

## Why
- Users need to list products by category without changing existing product behavior.
- The API should return a clear 404 when a category filter references a non-existent category.

## What Changes
- Add an optional categoryId filter to GET /products.
- When categoryId is provided and does not exist, return 404 with the standard error response.
- Preserve paging and sorting behavior for filtered results.

## Impact
- Affected specs: product-filtering
- Affected code: controller, service, repository, tests
- Behavior: adds optional filter parameter without changing default listing