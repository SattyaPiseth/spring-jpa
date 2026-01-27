package co.istad.springdatajpa.dto.request;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductUpdateRequest(
        @NotBlank
        @Size(max = 255)
        String name,
        @Size(max = 2000)
        String description,
        @NotNull
        @DecimalMin(value = "0.00", inclusive = false)
        BigDecimal price,
        UUID categoryId
) {
}

