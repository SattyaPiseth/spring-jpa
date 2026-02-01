package co.istad.springdatajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class VariantAttributeValueId implements Serializable {

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(name = "attribute_id")
    private UUID attributeId;

    public VariantAttributeValueId() {
    }

    public VariantAttributeValueId(UUID variantId, UUID attributeId) {
        this.variantId = variantId;
        this.attributeId = attributeId;
    }

    public UUID getVariantId() {
        return variantId;
    }

    public UUID getAttributeId() {
        return attributeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VariantAttributeValueId that = (VariantAttributeValueId) o;
        return Objects.equals(variantId, that.variantId) && Objects.equals(attributeId, that.attributeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variantId, attributeId);
    }
}
