package co.istad.springdatajpa.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummary(
        UUID id,
        String name,
        BigDecimal price
) {
}
