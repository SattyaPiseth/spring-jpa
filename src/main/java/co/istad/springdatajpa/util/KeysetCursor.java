package co.istad.springdatajpa.util;

import co.istad.springdatajpa.exception.BadRequestException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

public final class KeysetCursor {

    private static final String DELIMITER = "|";

    private KeysetCursor() {
    }

    public static String encode(Instant createdAt, UUID id) {
        if (createdAt == null || id == null) {
            throw new BadRequestException("cursor requires createdAt and id");
        }
        String payload = createdAt.toString() + DELIMITER + id;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    public static Decoded decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            throw new BadRequestException("cursor must not be blank");
        }
        try {
            String payload = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = payload.split("\\|", -1);
            if (parts.length != 2) {
                throw new BadRequestException("invalid cursor format");
            }
            Instant createdAt = Instant.parse(parts[0]);
            UUID id = UUID.fromString(parts[1]);
            return new Decoded(createdAt, id);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("invalid cursor format");
        }
    }

    public record Decoded(Instant createdAt, UUID id) {
    }
}
