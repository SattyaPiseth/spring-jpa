package co.istad.springdatajpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "categories",
        indexes = {
                @Index(name = "idx_categories_parent_id", columnList = "parent_id"),
                @Index(name = "idx_categories_parent_id_sort_order", columnList = "parent_id, sort_order")
        }
)
public class Category extends AuditedBaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255, unique = true)
    private String name;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"parent", "children", "products", "hibernateLazyInitializer", "handler"})
    private Category parent;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"parent", "children", "products", "hibernateLazyInitializer", "handler"})
    @Setter(AccessLevel.NONE)
    private List<Category> children = new ArrayList<>();

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"categories", "category", "legacyCategory", "hibernateLazyInitializer", "handler"})
    @Setter(AccessLevel.NONE)
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        if (product == null) {
            return;
        }
        if (product.getCategory() == null) {
            product.setCategory(this);
            return;
        }
        product.addCategory(this);
    }

    public void removeProduct(Product product) {
        if (product == null) {
            return;
        }
        product.removeCategory(this);
    }
}

