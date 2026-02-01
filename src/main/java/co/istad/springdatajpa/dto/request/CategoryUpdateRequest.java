package co.istad.springdatajpa.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CategoryUpdateRequest(
        @NotBlank
        @Size(max = 255)
        String name,
        @Size(max = 2000)
        String description,
        UUID parentId,
        @Min(0)
        Integer sortOrder
) {
}

