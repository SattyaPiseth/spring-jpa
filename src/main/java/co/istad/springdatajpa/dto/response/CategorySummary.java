package co.istad.springdatajpa.dto.response;

import java.util.UUID;

public record CategorySummary(
        UUID id,
        String name
) {
}

