package co.istad.springdatajpa.dto;

import java.util.UUID;

public record CategorySummary(
        UUID id,
        String name
) {
}
