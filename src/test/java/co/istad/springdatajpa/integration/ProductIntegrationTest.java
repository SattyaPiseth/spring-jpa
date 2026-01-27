package co.istad.springdatajpa.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.entity.Product;
import co.istad.springdatajpa.repository.CategoryRepository;
import co.istad.springdatajpa.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.Instant;
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

    @AfterEach
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void cleanDatabase() {
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

    private static Product newProduct(String name, String description, String price) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        return product;
    }

    private static Category newCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }
}


