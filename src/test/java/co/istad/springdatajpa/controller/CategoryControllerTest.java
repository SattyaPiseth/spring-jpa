package co.istad.springdatajpa.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.istad.springdatajpa.config.SpringDataWebConfig;
import co.istad.springdatajpa.dto.CategoryCreateRequest;
import co.istad.springdatajpa.dto.CategoryPatchRequest;
import co.istad.springdatajpa.dto.CategoryResponse;
import co.istad.springdatajpa.dto.CategoryUpdateRequest;
import co.istad.springdatajpa.error.RestExceptionHandler;
import co.istad.springdatajpa.exception.ResourceNotFoundException;
import co.istad.springdatajpa.service.CategoryService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
@Import({SpringDataWebConfig.class, RestExceptionHandler.class})
class CategoryControllerTest {

    private static final Instant CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T00:00:00Z");
    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void listCategories_returnsPage() throws Exception {
        CategoryResponse response = new CategoryResponse(
                UUID.randomUUID(),
                "Office",
                "Office supplies",
                CREATED_AT,
                UPDATED_AT
        );
        Page<CategoryResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
        when(categoryService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Office"))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1));
    }

    @Test
    void createCategory_validationFailure_returnsErrors() throws Exception {
        mockMvc.perform(post("/categories")
                        .contentType(JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").value(hasItem(containsString("name"))));
    }

    @Test
    void createCategory_success_returnsCreated() throws Exception {
        CategoryResponse response = new CategoryResponse(
                UUID.randomUUID(),
                "Office",
                "Office supplies",
                CREATED_AT,
                UPDATED_AT
        );
        when(categoryService.create(any(CategoryCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/categories")
                        .contentType(JSON)
                        .content("{\"name\":\"Office\",\"description\":\"Office supplies\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Office"));
    }

    @Test
    void updateCategory_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(categoryService.update(eq(id), any(CategoryUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException("Category not found: " + id));

        mockMvc.perform(put("/categories/{id}", id)
                        .contentType(JSON)
                        .content("{\"name\":\"Office\",\"description\":\"Office supplies\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void patchCategory_emptyPayload_returns400() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(patch("/categories/{id}", id)
                        .contentType(JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void patchCategory_descriptionTooLong_returns400() throws Exception {
        UUID id = UUID.randomUUID();
        String longDescription = "a".repeat(2001);
        mockMvc.perform(patch("/categories/{id}", id)
                        .contentType(JSON)
                        .content("{\"description\":\"" + longDescription + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void patchCategory_success_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        CategoryResponse response = new CategoryResponse(
                id,
                "Office",
                "Updated",
                CREATED_AT,
                UPDATED_AT
        );
        when(categoryService.patch(eq(id), any(CategoryPatchRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/categories/{id}", id)
                        .contentType(JSON)
                        .content("{\"description\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated"));
    }

    @Test
    void patchCategory_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(categoryService.patch(eq(id), any(CategoryPatchRequest.class)))
                .thenThrow(new ResourceNotFoundException("Category not found: " + id));

        mockMvc.perform(patch("/categories/{id}", id)
                        .contentType(JSON)
                        .content("{\"description\":\"Updated\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteCategory_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Category not found: " + id))
                .when(categoryService)
                .delete(id);

        mockMvc.perform(delete("/categories/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}
