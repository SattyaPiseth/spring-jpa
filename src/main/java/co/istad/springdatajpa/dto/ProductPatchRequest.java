package co.istad.springdatajpa.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductPatchRequest(
        @Pattern(regexp = ".*\\S.*", message = "name must not be blank")
        @Size(max = 255)
        String name,
        @Size(max = 2000)
        String description,
        @DecimalMin(value = "0.00", inclusive = false)
        BigDecimal price
) {
    @JsonIgnore
    @AssertTrue(message = "at least one field must be provided")
    public boolean isAnyFieldProvided() {
        return name != null || description != null || price != null;
    }
}
