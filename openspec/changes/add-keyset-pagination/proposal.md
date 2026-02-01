# Change: Add keyset pagination for products and variants

## Why
Offset pagination becomes inefficient at scale and is sensitive to inserts/deletes between pages. Keyset pagination provides stable, performant paging for large datasets.

## What Changes
- Add cursor-based keyset pagination to GET /products and GET /products/{id}/variants.
- Preserve existing page/size parameters and response shape for backward compatibility.
- Introduce an opaque cursor token encoding the last (createdAt, id) from the previous page.
- Provide keyset response shape with items + nextCursor (+ optional hasNext) when cursor is used.

## Impact
- Affected specs: product-filtering
- New specs: keyset-pagination
- Affected code: controllers, services, repositories, DTOs, tests
