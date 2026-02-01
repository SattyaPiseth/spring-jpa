package co.istad.springdatajpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
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
        name = "products",
        indexes = {
                @Index(name = "idx_products_primary_category_id", columnList = "primary_category_id"),
                @Index(name = "idx_products_legacy_category_id", columnList = "category_id")
        }
)
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
    @JoinColumn(name = "primary_category_id")
    @JsonIgnoreProperties({"products", "parent", "children", "hibernateLazyInitializer", "handler"})
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"products", "parent", "children", "hibernateLazyInitializer", "handler"})
    private Category legacyCategory;

    @ManyToMany
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"),
            indexes = {
                    @Index(name = "idx_product_categories_product_id", columnList = "product_id"),
                    @Index(name = "idx_product_categories_category_id", columnList = "category_id")
            }
    )
    @JsonIgnoreProperties({"products", "parent", "children", "hibernateLazyInitializer", "handler"})
    private Set<Category> categories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"product", "attributeValues", "hibernateLazyInitializer", "handler"})
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"product", "attribute", "hibernateLazyInitializer", "handler"})
    private Set<ProductAttributeValue> attributeValues = new LinkedHashSet<>();

    public void setCategory(Category category) {
        if (Objects.equals(this.category, category)) {
            return;
        }
        Category old = this.category;
        this.category = category;
        this.legacyCategory = category;
        if (old != null) {
            old.getProducts().remove(this);
            categories.remove(old);
        }
        if (category != null) {
            categories.add(category);
            if (!category.getProducts().contains(this)) {
                category.getProducts().add(this);
            }
        }
    }

    public void addCategory(Category category) {
        if (category == null) {
            return;
        }
        if (categories.add(category)) {
            category.getProducts().add(this);
        }
    }

    public void clearCategories() {
        List<Category> existing = new ArrayList<>(categories);
        for (Category category : existing) {
            removeCategory(category);
        }
    }

    public void removeCategory(Category category) {
        if (category == null) {
            return;
        }
        if (categories.remove(category)) {
            category.getProducts().remove(this);
        }
        if (Objects.equals(this.category, category)) {
            this.category = null;
            this.legacyCategory = null;
        }
    }

}

