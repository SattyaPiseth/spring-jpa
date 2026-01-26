package co.istad.springdatajpa.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.istad.springdatajpa.entity.Category;
import co.istad.springdatajpa.repository.CategoryRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void cleanDatabase() {
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
}

