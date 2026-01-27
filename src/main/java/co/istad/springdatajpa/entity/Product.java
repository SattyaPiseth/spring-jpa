package co.istad.springdatajpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product extends AuditedBaseEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "pro_name", nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"products", "hibernateLazyInitializer", "handler"})
    private Category category;

    public void setCategory(Category category) {
        if (Objects.equals(this.category, category)) {
            return;
        }
        Category old = this.category;
        this.category = category;
        if (old != null && old.getProducts() != null) {
            old.getProducts().remove(this);
        }
        if (category != null && category.getProducts() != null && !category.getProducts().contains(this)) {
            category.getProducts().add(this);
        }
    }

}

