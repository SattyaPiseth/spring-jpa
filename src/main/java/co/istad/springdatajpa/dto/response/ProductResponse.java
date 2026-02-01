package co.istad.springdatajpa.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        BigDecimal effectivePrice,
        UUID categoryId,
        CategorySummary category,
        Instant createdAt,
        Instant updatedAt
) {
}

