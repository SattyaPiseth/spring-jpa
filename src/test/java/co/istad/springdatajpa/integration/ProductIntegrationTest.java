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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void listProducts_smoke() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
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
                .andExpect(jsonPath("$.content[0].name").value("Laptop A"));
    }

    @Test
    void listProducts_categoryNotFound_returns404() throws Exception {
        mockMvc.perform(get("/products")
                        .param("categoryId", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
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
