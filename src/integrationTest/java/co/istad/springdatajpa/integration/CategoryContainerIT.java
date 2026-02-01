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
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class CategoryContainerIT {

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
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void cleanDatabase() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void patch_updatesDescriptionOnly() throws Exception {
        Category category = new Category();
        category.setName("Office");
        category.setDescription("Office supplies");
        Category saved = categoryRepository.saveAndFlush(category);
        UUID id = saved.getId();
        Instant createdAt = saved.getCreatedAt();
        Instant updatedAt = saved.getUpdatedAt();

        mockMvc.perform(patch("/categories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated"));

        Category updated = categoryRepository.findById(id).orElseThrow();
        assertThat(updated.getDescription()).isEqualTo("Updated");
        assertThat(updated.getName()).isEqualTo("Office");
        assertThat(updated.getCreatedAt()).isNotNull();
        assertThat(updated.getCreatedAt().truncatedTo(ChronoUnit.MILLIS))
                .isEqualTo(createdAt.truncatedTo(ChronoUnit.MILLIS));
        assertThat(updated.getUpdatedAt()).isNotNull();
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(updatedAt);
    }

    @Test
    void getCategory_includesProductSummaries() throws Exception {
        Category category = new Category();
        category.setName("Office");
        category.setDescription("Office supplies");
        Category savedCategory = categoryRepository.saveAndFlush(category);

        Product product = new Product();
        product.setName("Pen");
        product.setDescription("Blue ink");
        product.setPrice(new BigDecimal("1.25"));
        product.setCategory(savedCategory);
        Product savedProduct = productRepository.saveAndFlush(product);

        mockMvc.perform(get("/categories/{id}", savedCategory.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCategory.getId().toString()))
                .andExpect(jsonPath("$.products[0].id").value(savedProduct.getId().toString()))
                .andExpect(jsonPath("$.products[0].name").value("Pen"))
                .andExpect(jsonPath("$.products[0].price").value(1.25));
    }
}


