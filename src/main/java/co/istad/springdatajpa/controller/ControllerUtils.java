package co.istad.springdatajpa.controller;

import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

final class ControllerUtils {

    private ControllerUtils() {
    }

    static PageRequest pageRequest(int page, int size, String sort, Set<String> allowedFields, Sort defaultSort) {
        return PageRequest.of(page, size, parseSort(sort, allowedFields, defaultSort));
    }

    private static Sort parseSort(String sort, Set<String> allowedFields, Sort defaultSort) {
        if (sort == null || sort.isBlank()) {
            return defaultSort;
        }
        String[] parts = sort.split(",", 2);
        String property = parts[0].trim();
        if (!allowedFields.contains(property)) {
            return defaultSort;
        }
        if (parts.length == 2) {
            String direction = parts[1].trim();
            try {
                return Sort.by(Sort.Direction.fromString(direction), property);
            } catch (IllegalArgumentException ex) {
                return defaultSort;
            }
        }
        return Sort.by(Sort.Direction.DESC, property);
    }
}

