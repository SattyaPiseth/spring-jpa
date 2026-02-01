package co.istad.springdatajpa.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.entity.Product;
import co.istad.springdatajpa.entity.ProductVariant;
import co.istad.springdatajpa.repository.CategoryRepository;
import co.istad.springdatajpa.repository.ProductRepository;
import co.istad.springdatajpa.repository.ProductVariantRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.AfterEach;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void cleanDatabase() {
        productVariantRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void listProducts_smoke() throws Exception {
        String categoryName = "Office-" + UUID.randomUUID();
        Category category = newCategory(categoryName, "Office supplies");
        Category savedCategory = categoryRepository.saveAndFlush(category);

        Product product = newProduct("Notebook", "A5 notebook", "2.99");
        product.setCategory(savedCategory);
        productRepository.saveAndFlush(product);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].categoryId").value(savedCategory.getId().toString()))
                .andExpect(jsonPath("$.content[0].category.name").value(categoryName))
                .andExpect(jsonPath("$.page").exists());
    }

    @Test
    void repository_saveAndFind() {
        Product product = newProduct("Binder", "A4 binder", "9.99");
        productRepository.save(product);

        assertThat(productRepository.findAll())
                .isNotEmpty();
    }

    @Test
    void auditing_setsCreatedAndUpdatedAt() {
        Product product = newProduct("Stapler", "Office stapler", "3.50");
        Product saved = productRepository.saveAndFlush(product);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        Instant createdAt = saved.getCreatedAt();
        saved.setDescription("Office stapler - updated");
        Product updated = productRepository.saveAndFlush(saved);

        assertThat(updated.getUpdatedAt()).isNotNull();
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(createdAt);
    }

    @Test
    void patch_updatesOnlyProvidedFields() throws Exception {
        Product product = newProduct("Lamp", "Desk lamp", "12.50");
        Product saved = productRepository.saveAndFlush(product);
        UUID id = saved.getId();

        mockMvc.perform(patch("/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"price\":15.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(15.00))
                .andExpect(jsonPath("$.name").value("Lamp"));

        Product updated = productRepository.findById(id).orElseThrow();
        assertThat(updated.getPrice()).isEqualByComparingTo("15.00");
        assertThat(updated.getName()).isEqualTo("Lamp");
    }

    @Test
    void listProducts_withCategoryFilter_returnsOnlyMatching() throws Exception {
        Category category = newCategory("Laptops", "Laptop category");
        Category savedCategory = categoryRepository.saveAndFlush(category);

        Product laptop = newProduct("Laptop A", "Office laptop", "799.00");
        laptop.setCategory(savedCategory);
        productRepository.saveAndFlush(laptop);
        productRepository.saveAndFlush(newProduct("Mouse B", "Wireless mouse", "19.00"));

        mockMvc.perform(get("/products")
                        .param("categoryId", savedCategory.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].categoryId").value(savedCategory.getId().toString()))
                .andExpect(jsonPath("$.content[0].name").value("Laptop A"));
    }

    @Test
    void listProducts_categoryNotFound_returns404() throws Exception {
        mockMvc.perform(get("/products")
                        .param("categoryId", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getProduct_includesCategorySummary() throws Exception {
        Category category = newCategory("Accessories", "Office accessories");
        Category savedCategory = categoryRepository.saveAndFlush(category);

        Product product = newProduct("Pen", "Ballpoint pen", "1.25");
        product.setCategory(savedCategory);
        Product savedProduct = productRepository.saveAndFlush(product);

        mockMvc.perform(get("/products/{id}", savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category.id").value(savedCategory.getId().toString()))
                .andExpect(jsonPath("$.category.name").value("Accessories"));
    }

    @Test
    void getCategory_includesProductSummaries() throws Exception {
        Category category = newCategory("Storage", "Storage devices");
        Category savedCategory = categoryRepository.saveAndFlush(category);

        Product product = newProduct("SSD", "NVMe SSD", "99.00");
        product.setCategory(savedCategory);
        productRepository.saveAndFlush(product);

        mockMvc.perform(get("/categories/{id}", savedCategory.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products.length()").value(1))
                .andExpect(jsonPath("$.products[0].name").value("SSD"));
    }

    @Test
    void listCategories_productsEmptyByDefault() throws Exception {
        Category category = newCategory("Cables", "Cables and adapters");
        categoryRepository.saveAndFlush(category);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].products").isArray())
                .andExpect(jsonPath("$.content[0].products.length()").value(0));
    }

    @Test
    void bidirectional_helpers_keep_in_sync() {
        Category category = newCategory("Accessories", "Office accessories");
        Product product = newProduct("Pen", "Ballpoint pen", "1.25");

        category.addProduct(product);

        assertThat(product.getCategory()).isEqualTo(category);
        assertThat(category.getProducts()).contains(product);

        category.removeProduct(product);

        assertThat(product.getCategory()).isNull();
        assertThat(category.getProducts()).doesNotContain(product);
    }

    @Test
    void keyset_products_multiPageTraversal() throws Exception {
        productRepository.saveAndFlush(newProduct("Alpha", "A", "1.00"));
        productRepository.saveAndFlush(newProduct("Beta", "B", "2.00"));
        productRepository.saveAndFlush(newProduct("Gamma", "C", "3.00"));

        String firstResponse = mockMvc.perform(get("/products")
                        .param("size", "2")
                        .param("cursor", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode firstJson = objectMapper.readTree(firstResponse);
        String nextCursor = firstJson.get("nextCursor").asText();
        assertThat(nextCursor).isNotBlank();

        String secondResponse = mockMvc.perform(get("/products")
                        .param("size", "2")
                        .param("cursor", nextCursor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode secondJson = objectMapper.readTree(secondResponse);
        assertThat(secondJson.get("nextCursor").isNull()).isTrue();
    }

    @Test
    void keyset_products_invalidCursor_returns400() throws Exception {
        mockMvc.perform(get("/products")
                        .param("cursor", "not-base64")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void keyset_variants_multiPageTraversal() throws Exception {
        Product product = productRepository.saveAndFlush(newProduct("Phone", "Smartphone", "499.00"));
        productVariantRepository.saveAndFlush(newVariant(product, "SKU-A", "499.00", 10));
        productVariantRepository.saveAndFlush(newVariant(product, "SKU-B", "549.00", 5));

        String firstResponse = mockMvc.perform(get("/products/{id}/variants", product.getId())
                        .param("size", "1")
                        .param("cursor", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode firstJson = objectMapper.readTree(firstResponse);
        String nextCursor = firstJson.get("nextCursor").asText();
        assertThat(nextCursor).isNotBlank();

        mockMvc.perform(get("/products/{id}/variants", product.getId())
                        .param("size", "1")
                        .param("cursor", nextCursor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    private static Product newProduct(String name, String description, String price) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        return product;
    }

    private static ProductVariant newVariant(Product product, String sku, String price, int stock) {
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(sku);
        variant.setPrice(new BigDecimal(price));
        variant.setStock(stock);
        return variant;
    }

    private static Category newCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }
}


