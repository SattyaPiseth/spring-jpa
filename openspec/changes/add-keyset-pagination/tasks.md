## 1. Planning & Validation
- [ ] 1.1 Confirm cursor format and response shape fields (items, nextCursor, hasNext)
- [ ] 1.2 Validate OpenSpec change definition (openspec validate --strict)

## 2. API & DTOs
- [ ] 2.1 Add keyset response DTOs for products and variants
- [ ] 2.2 Add cursor parsing/encoding utilities with validation errors

## 3. Persistence & Queries
- [ ] 3.1 Add repository methods for keyset queries (createdAt/id)
- [ ] 3.2 Add fallback id-only keyset query where needed

## 4. Services
- [ ] 4.1 Implement keyset pagination in product listing
- [ ] 4.2 Implement keyset pagination in product-variant listing

## 5. Controllers
- [ ] 5.1 Accept cursor param and branch response shape for keyset mode
- [ ] 5.2 Preserve page/size offset mode

## 6. Tests
- [ ] 6.1 Add unit tests for cursor encode/decode
- [ ] 6.2 Add controller tests for keyset mode in /products and /products/{id}/variants
- [ ] 6.3 Add repository/service tests for keyset ordering and nextCursor
