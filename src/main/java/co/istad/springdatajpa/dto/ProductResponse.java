package co.istad.springdatajpa.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        UUID categoryId,
        CategorySummary category,
        Instant createdAt,
        Instant updatedAt
) {
}
