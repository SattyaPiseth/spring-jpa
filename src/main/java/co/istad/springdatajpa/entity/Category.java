package co.istad.springdatajpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categories")
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

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"category", "hibernateLazyInitializer", "handler"})
    @Setter(AccessLevel.NONE)
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        if (product == null) {
            return;
        }
        product.setCategory(this);
    }

    public void removeProduct(Product product) {
        if (product == null) {
            return;
        }
        if (product.getCategory() == this) {
            product.setCategory(null);
        } else {
            products.remove(product);
        }
    }
}
