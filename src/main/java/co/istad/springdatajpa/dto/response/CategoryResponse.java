package co.istad.springdatajpa.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String description,
        List<ProductSummary> products,
        Instant createdAt,
        Instant updatedAt
) {
}

