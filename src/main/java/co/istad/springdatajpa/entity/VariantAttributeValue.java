package co.istad.springdatajpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "variant_attribute_values",
        indexes = {
                @Index(name = "idx_variant_attribute_values_attribute_id", columnList = "attribute_id"),
                @Index(name = "idx_variant_attribute_values_variant_id", columnList = "variant_id")
        }
)
public class VariantAttributeValue {

    @EmbeddedId
    private VariantAttributeValueId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("variantId")
    @JoinColumn(name = "variant_id", nullable = false)
    @JsonIgnoreProperties({"attributeValues", "hibernateLazyInitializer", "handler"})
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("attributeId")
    @JoinColumn(name = "attribute_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AttributeDefinition attribute;

    @Column(name = "value_string", length = 2000)
    private String valueString;

    @Column(name = "value_number", precision = 19, scale = 4)
    private BigDecimal valueNumber;

    @Column(name = "value_boolean")
    private Boolean valueBoolean;
}
