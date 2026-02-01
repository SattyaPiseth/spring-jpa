package co.istad.springdatajpa.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductVariantCreateRequest(
        @NotBlank
        @Size(max = 255)
        String sku,
        @NotNull
        @DecimalMin(value = "0.00", inclusive = false)
        BigDecimal price,
        @NotNull
        @Min(0)
        Integer stock
) {
}
