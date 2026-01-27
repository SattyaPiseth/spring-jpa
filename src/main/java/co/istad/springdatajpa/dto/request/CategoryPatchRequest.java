package co.istad.springdatajpa.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

public record CategoryPatchRequest(
        @Size(max = 2000)
        String description
) {
    @JsonIgnore
    @AssertTrue(message = "at least one field must be provided")
    public boolean isAnyFieldProvided() {
        return description != null;
    }
}

