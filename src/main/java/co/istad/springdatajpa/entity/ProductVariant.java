package co.istad.springdatajpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "product_variants",
        indexes = {
                @Index(name = "idx_product_variants_product_id", columnList = "product_id"),
                @Index(name = "uq_product_variants_sku", columnList = "sku", unique = true)
        }
)
public class ProductVariant extends AuditedBaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"variants", "attributeValues", "hibernateLazyInitializer", "handler"})
    private Product product;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255, unique = true)
    private String sku;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    private Integer stock = 0;

    @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"variant", "attribute", "hibernateLazyInitializer", "handler"})
    private Set<VariantAttributeValue> attributeValues = new LinkedHashSet<>();
}
