package co.istad.springdatajpa.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record AttributeValueRequest(
        UUID attributeId,
        @Size(max = 2000)
        String valueString,
        BigDecimal valueNumber,
        Boolean valueBoolean
) {
    @JsonIgnore
    @AssertTrue(message = "exactly one attribute value must be provided")
    public boolean isExactlyOneValueProvided() {
        int count = 0;
        if (valueString != null) {
            count++;
        }
        if (valueNumber != null) {
            count++;
        }
        if (valueBoolean != null) {
            count++;
        }
        return count == 1;
    }
}
