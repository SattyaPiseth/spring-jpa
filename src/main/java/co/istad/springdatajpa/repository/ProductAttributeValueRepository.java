package co.istad.springdatajpa.repository;

import co.istad.springdatajpa.entity.ProductAttributeValue;
import co.istad.springdatajpa.entity.ProductAttributeValueId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, ProductAttributeValueId> {
    List<ProductAttributeValue> findAllByProductId(UUID productId);
}
