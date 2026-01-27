package co.istad.springdatajpa.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.istad.springdatajpa.dto.request.ProductCreateRequest;
import co.istad.springdatajpa.dto.request.ProductPatchRequest;
import co.istad.springdatajpa.dto.response.ProductResponse;
import co.istad.springdatajpa.dto.request.ProductUpdateRequest;
import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.entity.Product;
import co.istad.springdatajpa.exception.ResourceNotFoundException;
import co.istad.springdatajpa.mapper.ProductMapper;
import co.istad.springdatajpa.repository.CategoryRepository;
import co.istad.springdatajpa.repository.ProductRepository;
import co.istad.springdatajpa.service.impl.ProductServiceImpl;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void findById_success_returnsResponse() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        ProductResponse response = new ProductResponse(
                id,
                "Book",
                "Hardcover",
                new BigDecimal("19.99"),
                null,
                null,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-02T00:00:00Z")
        );
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse result = productService.findById(id);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void findById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(id, new ProductUpdateRequest("A", "B", BigDecimal.ONE, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_success_callsRepository() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        productService.delete(id);

        verify(productRepository).delete(product);
    }

    @Test
    void patch_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.patch(id, new ProductPatchRequest("A", null, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_success_mapsAndSaves() {
        ProductCreateRequest request = new ProductCreateRequest("Name", "Desc", new BigDecimal("2.50"), null);
        Product product = new Product();
        Product saved = new Product();
        ProductResponse response = new ProductResponse(
                UUID.randomUUID(),
                "Name",
                "Desc",
                new BigDecimal("2.50"),
                null,
                null,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-02T00:00:00Z")
        );
        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(saved);
        when(productMapper.toResponse(saved)).thenReturn(response);

        ProductResponse result = productService.create(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void create_withCategory_setsCategoryBeforeSave() {
        UUID categoryId = UUID.randomUUID();
        ProductCreateRequest request = new ProductCreateRequest("Name", "Desc", new BigDecimal("2.50"), categoryId);
        Product product = new Product();
        Category category = new Category();

        when(productMapper.toEntity(request)).thenReturn(product);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.save(product)).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toResponse(product)).thenReturn(new ProductResponse(
                UUID.randomUUID(),
                "Name",
                "Desc",
                new BigDecimal("2.50"),
                categoryId,
                null,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-02T00:00:00Z")
        ));

        productService.create(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getCategory()).isEqualTo(category);
    }
}

