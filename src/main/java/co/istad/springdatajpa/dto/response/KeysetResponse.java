package co.istad.springdatajpa.dto.response;

import java.util.List;

public record KeysetResponse<T>(
        List<T> items,
        String nextCursor,
        boolean hasNext
) {
}
