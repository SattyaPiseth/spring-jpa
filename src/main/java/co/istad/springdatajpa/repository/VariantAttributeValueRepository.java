package co.istad.springdatajpa.repository;

import co.istad.springdatajpa.entity.VariantAttributeValue;
import co.istad.springdatajpa.entity.VariantAttributeValueId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariantAttributeValueRepository extends JpaRepository<VariantAttributeValue, VariantAttributeValueId> {
    List<VariantAttributeValue> findAllByVariantId(UUID variantId);
}
