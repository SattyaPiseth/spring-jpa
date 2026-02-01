package co.istad.springdatajpa.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CategoryPatchRequest(
        @Size(max = 2000)
        String description,
        UUID parentId,
        @Min(0)
        Integer sortOrder
) {
    @JsonIgnore
    @AssertTrue(message = "at least one field must be provided")
    public boolean isAnyFieldProvided() {
        return description != null || parentId != null || sortOrder != null;
    }
}

