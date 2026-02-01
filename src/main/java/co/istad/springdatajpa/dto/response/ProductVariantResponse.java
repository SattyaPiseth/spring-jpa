package co.istad.springdatajpa.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductVariantResponse(
        UUID id,
        UUID productId,
        String sku,
        BigDecimal price,
        BigDecimal effectivePrice,
        Integer stock,
        Instant createdAt,
        Instant updatedAt
) {
}
