package co.istad.springdatajpa.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductCreateRequest(
        @NotBlank
        @Size(max = 255)
        String name,
        @Size(max = 2000)
        String description,
        @NotNull
        @DecimalMin(value = "0.00", inclusive = true)
        BigDecimal price
) {
}
