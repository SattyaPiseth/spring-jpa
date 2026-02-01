## Context
The API currently uses offset pagination via Spring Data PageRequest. We need to introduce keyset pagination for high-scale paging while keeping existing page/size behavior for backward compatibility.

## Goals / Non-Goals
- Goals:
  - Add keyset pagination for GET /products and GET /products/{id}/variants.
  - Use stable sort by createdAt DESC, id DESC (fallback id DESC when createdAt is unavailable).
  - Keep page/size offset pagination intact for existing clients.
  - Use an opaque cursor token encoding the last (createdAt, id).
- Non-Goals:
  - Replacing offset pagination across all endpoints.
  - Adding generic keyset pagination framework across the entire API.

## Decisions
- Decision: Dual-mode pagination per endpoint.
  - If `cursor` is provided, return keyset response shape.
  - If `cursor` is not provided, keep existing offset response shape.
- Decision: Cursor encoding contains lastSeenCreatedAt + lastSeenId.
  - Use a URL-safe Base64 token of a compact payload (e.g., "<createdAt>|<id>") to keep it opaque.
- Decision: Sort order is createdAt DESC, id DESC.
  - If a dataset lacks createdAt, sort by id DESC.

## Keyset Response Shape
For cursor-based requests:
```json
{
  "items": [ ... ],
  "nextCursor": "opaque-token",
  "hasNext": true
}
```

## Query Strategy
- Use a predicate of (createdAt < lastCreatedAt) OR (createdAt = lastCreatedAt AND id < lastId)
  with DESC order on createdAt and id.
- When createdAt is not available, use id < lastId with DESC order on id.

## Migration Plan
No data migration required. This is API-layer and query logic only.

## Risks / Trade-offs
- Dual response shapes increase client complexity; we mitigate by documenting cursor usage as recommended.
- Cursor decoding errors should return 400 with a clear message.

## Open Questions
None.
