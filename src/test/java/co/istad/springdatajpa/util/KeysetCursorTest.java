package co.istad.springdatajpa.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.istad.springdatajpa.exception.BadRequestException;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class KeysetCursorTest {

    @Test
    void encodeDecode_roundTrip() {
        Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");
        UUID id = UUID.randomUUID();

        String cursor = KeysetCursor.encode(createdAt, id);
        KeysetCursor.Decoded decoded = KeysetCursor.decode(cursor);

        assertThat(decoded.createdAt()).isEqualTo(createdAt);
        assertThat(decoded.id()).isEqualTo(id);
    }

    @Test
    void decode_invalid_throwsBadRequest() {
        assertThatThrownBy(() -> KeysetCursor.decode("not-base64"))
                .isInstanceOf(BadRequestException.class);
    }
}
