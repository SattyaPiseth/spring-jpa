package co.istad.springdatajpa.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.entity.Product;
import co.istad.springdatajpa.repository.CategoryRepository;
import co.istad.springdatajpa.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@EnabledIfSystemProperty(named = "it.tc", matches = "true")
@Transactional
class ProductContainerIT {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

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
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").exists());
    }

    @Test
    void listProducts_includesCategorySummary() throws Exception {
        Category category = new Category();
        category.setName("Office");
        category.setDescription("Office supplies");
        Category savedCategory = categoryRepository.saveAndFlush(category);

        Product product = newProduct("Folder", "Poly folder", "2.50");
        product.setCategory(savedCategory);
        Product savedProduct = productRepository.saveAndFlush(product);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(savedProduct.getId().toString()))
                .andExpect(jsonPath("$.content[0].categoryId").value(savedCategory.getId().toString()))
                .andExpect(jsonPath("$.content[0].category.id").value(savedCategory.getId().toString()))
                .andExpect(jsonPath("$.content[0].category.name").value("Office"));
    }

    @Test
    void repository_saveAndFind() {
        Product product = newProduct("Folder", "Poly folder", "2.50");
        productRepository.save(product);

        assertThat(productRepository.findAll())
                .isNotEmpty();
    }

    private static Product newProduct(String name, String description, String price) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        return product;
    }
}


