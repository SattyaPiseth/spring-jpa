package co.istad.springdatajpa.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import co.istad.springdatajpa.dto.response.CategorySummary;
import co.istad.springdatajpa.dto.response.ProductResponse;
import co.istad.springdatajpa.dto.response.ProductSummary;
import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.entity.Product;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void toResponse_mapsCategoryIdAndSummary() {
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Office");

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Mouse");
        product.setDescription("Wireless");
        product.setPrice(new BigDecimal("49.99"));
        product.setCreatedAt(Instant.parse("2025-01-01T00:00:00Z"));
        product.setUpdatedAt(Instant.parse("2025-01-02T00:00:00Z"));
        product.setCategory(category);

        ProductResponse response = mapper.toResponse(product);

        assertThat(response.categoryId()).isEqualTo(categoryId);
        assertThat(response.category()).isEqualTo(new CategorySummary(categoryId, "Office"));
        assertThat(response.effectivePrice()).isEqualByComparingTo("49.99");
    }

    @Test
    void toSummary_mapsProductFields() {
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setId(productId);
        product.setName("Pen");
        product.setPrice(new BigDecimal("1.25"));

        ProductSummary summary = mapper.toSummary(product);

        assertThat(summary.id()).isEqualTo(productId);
        assertThat(summary.name()).isEqualTo("Pen");
        assertThat(summary.price()).isEqualByComparingTo("1.25");
    }
}
