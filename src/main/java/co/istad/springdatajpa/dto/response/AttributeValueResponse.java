package co.istad.springdatajpa.dto.response;

import co.istad.springdatajpa.entity.AttributeDataType;
import co.istad.springdatajpa.entity.AttributeScope;
import java.math.BigDecimal;
import java.util.UUID;

public record AttributeValueResponse(
        UUID attributeId,
        AttributeDataType dataType,
        AttributeScope scope,
        String valueString,
        BigDecimal valueNumber,
        Boolean valueBoolean
) {
}
