package co.istad.springdatajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ProductAttributeValueId implements Serializable {

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "attribute_id")
    private UUID attributeId;

    public ProductAttributeValueId() {
    }

    public ProductAttributeValueId(UUID productId, UUID attributeId) {
        this.productId = productId;
        this.attributeId = attributeId;
    }

    public UUID getProductId() {
        return productId;
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
        ProductAttributeValueId that = (ProductAttributeValueId) o;
        return Objects.equals(productId, that.productId) && Objects.equals(attributeId, that.attributeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, attributeId);
    }
}
