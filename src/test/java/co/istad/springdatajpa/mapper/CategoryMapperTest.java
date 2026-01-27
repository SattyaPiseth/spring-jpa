package co.istad.springdatajpa.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import co.istad.springdatajpa.dto.response.CategoryResponse;
import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.entity.Product;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class CategoryMapperTest {

    private final CategoryMapper mapper = Mappers.getMapper(CategoryMapper.class);
    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @BeforeEach
    void wireMappers() throws Exception {
        if (mapper instanceof CategoryMapperImpl impl) {
            Field field = CategoryMapperImpl.class.getDeclaredField("productMapper");
            field.setAccessible(true);
            field.set(impl, productMapper);
        }
    }

    @Test
    void toResponse_mapsProductSummaries() {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Office");
        category.setDescription("Office supplies");
        category.setCreatedAt(Instant.parse("2025-01-01T00:00:00Z"));
        category.setUpdatedAt(Instant.parse("2025-01-02T00:00:00Z"));

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Pen");
        product.setPrice(new BigDecimal("1.25"));
        category.addProduct(product);

        CategoryResponse response = mapper.toResponse(category);

        assertThat(response.products()).hasSize(1);
        assertThat(response.products().get(0).id()).isEqualTo(product.getId());
        assertThat(response.products().get(0).name()).isEqualTo("Pen");
        assertThat(response.products().get(0).price()).isEqualByComparingTo("1.25");
    }

    @Test
    void toResponseWithoutProducts_returnsEmptyList() {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Office");

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Pen");
        product.setPrice(new BigDecimal("1.25"));
        category.addProduct(product);

        CategoryResponse response = mapper.toResponseWithoutProducts(category);

        assertThat(response.products()).isNotNull();
        assertThat(response.products()).isEmpty();
    }
}
